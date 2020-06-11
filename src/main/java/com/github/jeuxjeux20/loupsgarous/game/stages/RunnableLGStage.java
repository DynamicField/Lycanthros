package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * The runnable implementation of {@link LGStage}.
 */
public abstract class RunnableLGStage implements LGStage, Terminable, TerminableConsumer {
    protected final LGGameOrchestrator orchestrator;
    private final CompositeTerminable compositeTerminable = CompositeTerminable.create();

    private @Nullable CompletableFuture<Void> currentFuture;

    @Inject
    public RunnableLGStage(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public final CompletableFuture<Void> run() {
        Preconditions.checkState(currentFuture == null, "This stage has already been ran, or it has closed.");

        currentFuture = execute();

        // Close all the terminables just after the task finishes,
        // successfully or not.
        currentFuture.whenComplete((r, u) -> compositeTerminable.closeAndReportException());

        return currentFuture;
    }

    protected abstract CompletableFuture<Void> execute();

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
            compositeTerminable.close();
        }
        else if (!currentFuture.isDone()) {
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
        return compositeTerminable.bind(terminable);
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("orchestrator", orchestrator)
                .append("currentFuture", currentFuture)
                .toString();
    }

    public interface Factory<T extends RunnableLGStage> {
        T create(LGGameOrchestrator gameOrchestrator);
    }
}
