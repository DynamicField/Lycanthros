package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.assistedinject.Assisted;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class AsyncLGGameStage implements LGGameStage {
    protected final static CompletableFuture<Void> COMPLETED = CompletableFuture.completedFuture(null);
    protected final LGGameOrchestrator orchestrator;

    public AsyncLGGameStage(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    protected CompletableFuture<Void> cancelRoot(CompletableFuture<Void> root,
                                                 Function<? super CompletableFuture<Void>,
                                                         ? extends CompletableFuture<Void>> additionalOperations) {
        CompletableFuture<Void> withOperations = additionalOperations.apply(root);

        withOperations.whenComplete((r, t) -> root.cancel(true));

        return withOperations;
    }

    public abstract CompletableFuture<Void> run();

    public boolean shouldRun() {
        return true;
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    public interface Factory<T extends AsyncLGGameStage> {
        T create(LGGameOrchestrator gameOrchestrator);
    }
}
