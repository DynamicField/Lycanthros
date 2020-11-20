package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.event.phase.*;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.storage.MapStorage;
import com.github.jeuxjeux20.loupsgarous.storage.Storage;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.SingleSubject;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * The runnable implementation of {@link Phase}.
 */
public abstract class RunnablePhase implements Phase {
    protected final LGGameOrchestrator orchestrator;
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    private final SingleSubject<PhaseTerminationMethod> earlyTermination = SingleSubject.create();

    private @Nullable LGPhaseStartingEvent currentStartingEvent;
    private @Nullable PhaseTerminationMethod terminationMethod;
    private final PublishSubject<State> stateSubject = PublishSubject.create();
    private State state = State.READY;
    private PhaseTask runningTask;

    private PhaseDescriptor descriptor;
    private final Storage storage = new MapStorage();

    @Inject
    public RunnablePhase(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    final Completable run() {
        Preconditions.checkState(state == State.READY, "This phase is not ready.");

        Single<PhaseTerminationMethod> task = doRun().cache();

        Disposable subscription = task.subscribe((method, e) -> {
            setState(State.ENDING);
            terminationMethod = method;
            if (runningTask != null && runningTask.isRunning()) {
                if (!runningTask.stop()) {
                    orchestrator.logger().warning("Failed to stop phase task " + runningTask);
                }
            }
            runningTask = null;
            if (method == PhaseTerminationMethod.NORMAL) {
                Events.call(new LGPhaseEndingEvent(this));
                finish();
                Events.call(new LGPhaseEndedEvent(this));
            } else if (method == PhaseTerminationMethod.INTERRUPTED) {
                Events.call(new LGPhaseInterruptedEvent(this));
            }
            terminate();
        });
        bind(subscription::dispose);

        return Completable.fromSingle(task);
    }

    private Single<PhaseTerminationMethod> doRun() {
        setState(State.PREPARING);

        if (!shouldRun()) {
            return Single.just(PhaseTerminationMethod.NOT_RAN);
        }

        callStartingEvent();
        if (earlyTermination.hasValue()) {
            return earlyTermination;
        }

        // Now, start the phase!
        try {
            runningTask = execute();
        } catch (Throwable e) {
            return Single.error(e);
        }

        setState(State.RUNNING);
        Events.call(new LGPhaseStartedEvent(this));

        Single<PhaseTerminationMethod> normalTermination
                = runningTask.getCompletable().andThen(Single.just(PhaseTerminationMethod.NORMAL));

        return normalTermination.ambWith(earlyTermination);
    }

    private void callStartingEvent() {
        currentStartingEvent = new LGPhaseStartingEvent(this);
        Events.call(currentStartingEvent);

        if (currentStartingEvent.isCancelled()) {
            earlyTermination.onSuccess(PhaseTerminationMethod.CANCELLED);
        }
    }

    protected abstract PhaseTask execute();

    protected void finish() {
    }

    protected void cleanup() {
    }

    protected boolean shouldRun() {
        return true;
    }

    private void terminate() {
        if (state == State.TERMINATED) {
            return;
        }
        setState(State.TERMINATED);

        cleanup();
        terminableRegistry.closeAndReportException();
    }

    @Override
    public boolean stop() {
        if (state == State.RUNNING) {
            return runningTask.stop();
        } else {
            return false;
        }
    }

    @Override
    public boolean interrupt() {
        if (!state.isInterruptionPossible()) {
            return false;
        }

        switch (state) {
            case READY:
                terminate();
                return true;
            case PREPARING:
                if (currentStartingEvent != null) {
                    currentStartingEvent.setCancelled(true);
                }
                return true;
            case RUNNING:
                earlyTermination.onSuccess(PhaseTerminationMethod.INTERRUPTED);
                return true;
            default:
                throw new UnsupportedOperationException(
                        "Unsupported state termination: " + state);
        }
    }

    @Override
    public final State getState() {
        return state;
    }

    public void setState(State state) {
        if (this.state == state) {
            return;
        }

        this.state = state;
        stateSubject.onNext(state);
        if (state == State.TERMINATED) {
            stateSubject.onComplete();
        }
    }

    public Observable<State> stateChanges() {
        return stateSubject;
    }

    @Override
    public @Nullable PhaseTerminationMethod getTerminationMethod() {
        return terminationMethod;
    }

    @Override
    public PhaseDescriptor getDescriptor() {
        if (descriptor == null) {
            descriptor = PhaseDescriptor.fromClass(getClass());
        }
        return descriptor;
    }

    @Override
    public Storage getStorage() {
        return storage;
    }

    @Override
    @Nonnull
    public final <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @Override
    public final LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orchestrator", orchestrator)
                .add("state", state)
                .add("terminationMethod", terminationMethod)
                .toString();
    }

    protected static abstract class PhaseTask {
        private final Completable completable;

        protected PhaseTask(Completable completable) {
            this.completable = completable;
        }

        public Completable getCompletable() {
            return completable;
        }

        public abstract boolean isRunning();

        public abstract boolean stop();
    }
}
