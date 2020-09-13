package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseEndedEvent;
import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseEndingEvent;
import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseStartedEvent;
import com.github.jeuxjeux20.loupsgarous.event.phase.LGPhaseStartingEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.OrderTransformer;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.util.CompletableFutures;
import com.github.jeuxjeux20.relativesorting.Order;
import com.github.jeuxjeux20.relativesorting.OrderedElement;
import com.github.jeuxjeux20.relativesorting.OrderedElementTransformer;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * The runnable implementation of {@link LGPhase}.
 */
public abstract class RunnableLGPhase implements LGPhase, Terminable {
    protected final LGGameOrchestrator orchestrator;
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    private @Nullable CompletableFuture<Void> currentFuture;
    private @Nullable LGPhaseStartingEvent currentStartingEvent;
    private boolean isClosed;

    @Inject
    public RunnableLGPhase(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public static <T extends RunnableLGPhase> SortableFactory<T> createPhaseFactory(
            Class<T> phaseClass) {
        return new SortableFactory.ByClass<>(phaseClass);
    }

    public final CompletableFuture<Void> run() {
        Preconditions.checkState(!isClosed, "This phase has already been ran, or it has closed.");

        callStartingEvent();

        // The event has been cancelled OR the phase has been cancelled
        // in either case: ensure we close the phase and return a cancelled future.
        if (isClosed) {
            return CompletableFutures.cancelledFuture();
        }

        // Now, start the phase!

        CompletableFuture<Void> initialFuture = execute();
        currentFuture = initialFuture;

        Events.call(new LGPhaseStartedEvent(this));

        CompletableFuture<Void> future = initialFuture
                .thenRun(this::throwIfClosedForUnsupportedInterruption)
                .thenRun(() -> Events.call(new LGPhaseEndingEvent(this)))
                .thenRun(this::finish)
                .thenRun(() -> Events.call(new LGPhaseEndedEvent(this)))
                .whenComplete((r, u) -> closeAndReportException());

        // Cancel the initial future when this gets cancelled.
        future.whenComplete((r, u) -> initialFuture.cancel(true));

        return future;
    }

    private void callStartingEvent() {
        currentStartingEvent = new LGPhaseStartingEvent(this);
        Events.call(currentStartingEvent);

        if (currentStartingEvent.isCancelled()) {
            closeAndReportException();
        }
    }

    protected abstract CompletableFuture<Void> execute();

    protected void finish() {
    }

    public boolean shouldRun() {
        return true;
    }

    @Override
    public final void close() throws Exception {
        if (isClosed) {
            return;
        }
        isClosed = true;

        orchestrator.logger().finer("Closing phase " + super.toString() + "...");

        if (currentFuture != null) {
            if (supportsInterruption()) {
                currentFuture.cancel(true);
            } else {
                ensureFutureComplete();
            }
        }

        if (currentStartingEvent != null) {
            currentStartingEvent.setCancelled(true);
        }

        terminableRegistry.close();
    }

    private void ensureFutureComplete() {
        if (currentFuture == null) {
            return;
        }
        try {
            currentFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            // We just want it to complete.
        }
    }

    @Override
    public final boolean isClosed() {
        return isClosed;
    }

    /**
     * Returns whether or not this phase supports interruption on its future.
     * <p>
     * A value of {@code true} (the default) will cancel the future in {@link #close()} and will
     * manually complete the future in {@link #stop()}.<br> A value of {@code false} will wait until
     * the future completed in both methods.
     *
     * @return whether or not this phase supports interruption
     */
    protected boolean supportsInterruption() {
        return true;
    }

    private void throwIfClosedForUnsupportedInterruption() {
        if (!supportsInterruption() && isClosed) {
            throw new CancellationException();
        }
    }

    @Override
    public final boolean stop() {
        if (currentFuture != null && !currentFuture.isDone()) {
            if (supportsInterruption()) {
                return currentFuture.complete(null);
            } else {
                ensureFutureComplete();
                return true;
            }
        }
        return false;
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
                .add("currentFuture", currentFuture)
                .toString();
    }

    public abstract static class Factory<T extends RunnableLGPhase> {
        public abstract T create(LGGameOrchestrator gameOrchestrator);

        public abstract String getName();

        @Override
        public String toString() {
            return getName();
        }
    }

    @OrderTransformer(SortableFactory.OrderTransformer.class)
    public abstract static class SortableFactory<T extends RunnableLGPhase> extends Factory<T> {
        public abstract Class<?> getIdentifier();

        @Override
        public String getName() {
            return getIdentifier().getName();
        }

        private static final class ByClass<T extends RunnableLGPhase> extends SortableFactory<T> {
            private final Class<T> phaseClass;
            private final Constructor<T> phaseConstructor;

            private ByClass(Class<T> phaseClass) {
                this.phaseClass = phaseClass;
                try {
                    this.phaseConstructor = phaseClass.getConstructor(LGGameOrchestrator.class);
                } catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException(
                            "Class " + phaseClass + "does not have a public constructor " +
                            "taking a LGGameOrchestrator argument."
                    );
                }
            }

            @Override
            public T create(LGGameOrchestrator gameOrchestrator) {
                try {
                    return phaseConstructor.newInstance(gameOrchestrator);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate " + phaseClass + ".", e);
                }
            }

            @Override
            public Class<?> getIdentifier() {
                return phaseClass;
            }
        }

        private static final class OrderTransformer implements OrderedElementTransformer {
            public OrderTransformer() {
            }

            @Override
            public <T> OrderedElement<T> transform(OrderedElement<T> orderedElement) {
                Class<?> clazz = ((SortableFactory<?>) orderedElement.getElement()).getIdentifier();
                return orderedElement
                        .change(its -> its.identifier(clazz)
                                .order(clazz.getAnnotation(Order.class)));
            }
        }
    }
}
