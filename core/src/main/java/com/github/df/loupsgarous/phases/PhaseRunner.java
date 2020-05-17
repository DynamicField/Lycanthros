package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.logging.Level;

public class PhaseRunner {
    private final LGGameOrchestrator orchestrator;
    private @Nullable RunToken current;
    private @Nullable Disposable currentSubscription;
    private final PublishSubject<Optional<RunToken>> currentSubject = PublishSubject.create();

    PhaseRunner(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public @Nullable RunToken getCurrent() {
        return current;
    }

    private void setCurrent(@Nullable RunToken current) {
        try {
            terminateCurrent();
        } catch (PhaseTransitioningException e) {
            orchestrator.logger().log(Level.WARNING,
                    "Discarding current phase while it is transitioning.", e);
        }
        this.current = current;
        currentSubject.onNext(Optional.ofNullable(current));
    }

    public Observable<Optional<RunToken>> currentUpdates() {
        return currentSubject;
    }

    public Single<PhaseResult> run(RunnablePhase phase) {
        return run(new RunToken(phase, null));
    }

    public Single<PhaseResult> run(RunnablePhase phase, Object source) {
        return run(new RunToken(phase, source));
    }

    public Single<PhaseResult> run(RunToken token) {
        if (token.getPhase().getOrchestrator() != orchestrator) {
            throw new IllegalArgumentException(
                    "The given RunToken phase has been ran on the wrong PhaseRunner."
            );
        }

        RunnablePhase phase = token.getPhase();
        setCurrent(token);

        Single<PhaseResult> phaseTask = phase.run()
                .toSingle(() -> new PhaseResult(token, phase.getTerminationMethod()))
                .doOnSuccess(r -> orchestrator.logger()
                        .fine("Phase " + phase + " terminated: " + r.getTerminationMethod()))
                .doOnError(e -> orchestrator.logger()
                        .log(Level.WARNING, "Phase " + phase + " failed to execute.", e))
                .cache();

        if (currentSubscription != null) {
            currentSubscription.dispose();
        }

        currentSubscription = phaseTask.subscribe((result, e) -> {
            if (current == token) {
                clearCurrent();
            }
        });

        return phaseTask;
    }

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    public void terminateCurrent() throws PhaseTransitioningException {
        if (current != null) {
            RunnablePhase phase = current.getPhase();

            if (phase.getState() == Phase.State.TERMINATED || phase.interrupt()) {
                clearCurrent();
            } else {
                throw new PhaseTransitioningException(
                        "Cannot terminate current phase " + phase + " while in a " +
                        "transitioning state (" + phase.getState() + ")");
            }
        }
    }

    private void clearCurrent() {
        current = null;
        if (currentSubscription != null) {
            currentSubscription.dispose();
        }
    }

    public static final class RunToken {
        private final RunnablePhase phase;
        private final Object source;

        public RunToken(RunnablePhase phase, Object source) {
            this.phase = phase;
            this.source = source;
        }

        public RunnablePhase getPhase() {
            return phase;
        }

        public Object getSource() {
            return source;
        }
    }
}
