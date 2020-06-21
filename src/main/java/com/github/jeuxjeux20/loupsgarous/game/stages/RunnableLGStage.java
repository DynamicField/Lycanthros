package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
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

    @Inject
    public RunnableLGStage(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public final Task run() {
        Preconditions.checkState(currentFuture == null, "This stage has already been ran, or it has closed.");

        currentFuture = execute();

        CompletableFuture<Void> beforeFinish = currentFuture;

        CompletableFuture<Void> main = beforeFinish
                .thenRun(this::finish)
                .whenComplete((r, u) -> terminableRegistry.closeAndReportException());

        // Cancel the real future if something manually completes the given future
        // (cancelling, for example)
        main.whenComplete((r, u) -> currentFuture.cancel(true));

        return new Task(main, beforeFinish);
    }

    protected abstract CompletableFuture<Void> execute();

    protected void finish() {}

    public boolean shouldRun() {
        return true;
    }

    @Override
    public final void close() throws Exception {
        if (currentFuture == null) {
            // Create a cancelled future.
            CompletableFuture<Void> cancelledFuture = new CompletableFuture<>();
            cancelledFuture.cancel(false);
            currentFuture = cancelledFuture;

            // Close the terminables as we won't be able to start the stage.
            terminableRegistry.close();
        } else if (!currentFuture.isDone()) {
            currentFuture.cancel(true);
        }
        // Don't do anything if the stage has already ended.
    }

    @Override
    public final boolean isClosed() {
        return currentFuture != null && currentFuture.isDone();
    }

    @Override
    @Nonnull
    public final <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
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

    /**
     * An execution of a stage, regrouping multiple {@link CompletableFuture}s.
     */
    public static class Task {
        private final CompletableFuture<Void> main;
        private final CompletableFuture<Void> beforeFinish;

        private Task(CompletableFuture<Void> main, CompletableFuture<Void> beforeFinish) {
            this.main = main;
            this.beforeFinish = beforeFinish;
        }

        public CompletableFuture<Void> main() {
            return main;
        }

        public CompletableFuture<Void> beforeFinish() {
            return beforeFinish;
        }
    }
}
