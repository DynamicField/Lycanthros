package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.assistedinject.Assisted;

import java.util.concurrent.CompletableFuture;

public abstract class AsyncLGGameStage implements LGGameStage {
    protected final static CompletableFuture<Void> COMPLETED = CompletableFuture.completedFuture(null);
    protected final LGGameOrchestrator orchestrator;

    public AsyncLGGameStage(@Assisted LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
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
