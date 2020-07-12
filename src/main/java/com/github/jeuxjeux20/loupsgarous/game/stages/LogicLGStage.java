package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

import java.util.concurrent.CompletableFuture;

public abstract class LogicLGStage extends RunnableLGStage {
    public LogicLGStage(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public final CompletableFuture<Void> execute() {
        start();
        return CompletableFuture.completedFuture(null);
    }

    public abstract void start();

    @Override
    protected boolean supportsInterruption() {
        return false;
    }

    @Override
    public boolean isLogic() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }
}
