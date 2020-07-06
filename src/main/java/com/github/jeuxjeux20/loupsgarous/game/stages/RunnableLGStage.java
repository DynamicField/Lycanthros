package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageEndedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageEndingEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageStartedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageStartingEvent;
import com.github.jeuxjeux20.loupsgarous.util.CompletableFutures;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * The runnable implementation of {@link LGStage}.
 */
public abstract class RunnableLGStage implements LGStage, Terminable, TerminableConsumer {
    protected final LGGameOrchestrator orchestrator;
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    private @Nullable CompletableFuture<Void> currentFuture;
    private @Nullable LGStageStartingEvent currentStartingEvent;
    private boolean isClosed;

    @Inject
    public RunnableLGStage(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public final CompletableFuture<Void> run() {
        Preconditions.checkState(!isClosed, "This stage has already been ran, or it has closed.");

        callStartingEvent();

        // The event has been cancelled OR the stage has been cancelled
        // in either case: ensure we close the stage and return a cancelled future.
        if (isClosed) {
            return CompletableFutures.cancelledFuture();
        }

        // Now, start the stage!

        CompletableFuture<Void> initialFuture = execute();
        currentFuture = initialFuture;

        Events.call(new LGStageStartedEvent(this));

        CompletableFuture<Void> future = initialFuture
                .thenRun(() -> Events.call(new LGStageEndingEvent(this)))
                .thenRun(this::finish)
                .thenRun(() -> Events.call(new LGStageEndedEvent(this)))
                .whenComplete((r, u) -> closeAndReportException());

        // Cancel the initial future when this gets cancelled.
        future.whenComplete((r, u) -> initialFuture.cancel(true));

        return future;
    }

    private void callStartingEvent() {
        currentStartingEvent = new LGStageStartingEvent(this);
        Events.call(currentStartingEvent);

        if (currentStartingEvent.isCancelled()) {
            closeAndReportException();
        }
    }

    protected abstract CompletableFuture<Void> execute();

    protected void finish() {}

    public boolean shouldRun() {
        return true;
    }

    @Override
    public final void close() throws Exception {
        if (isClosed) {
            return;
        }
        isClosed = true;

        orchestrator.logger().finer("Closing stage " +
                                    getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) +
                                    "...");

        if (currentFuture != null) {
            currentFuture.cancel(true);
        }

        if (currentStartingEvent != null) {
            currentStartingEvent.setCancelled(true);
        }

        terminableRegistry.close();
    }

    @Override
    public final boolean isClosed() {
        return isClosed;
    }

    @Override
    @Nonnull
    public final <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @Override
    public final LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orchestrator", orchestrator)
                .add("currentFuture", currentFuture)
                .toString();
    }

    public interface Factory<T extends RunnableLGStage> {
        T create(LGGameOrchestrator gameOrchestrator);
    }
}
