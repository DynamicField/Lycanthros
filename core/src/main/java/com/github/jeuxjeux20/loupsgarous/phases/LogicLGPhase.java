package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

import java.util.concurrent.CompletableFuture;

public abstract class LogicLGPhase extends RunnableLGPhase {
    public LogicLGPhase(LGGameOrchestrator orchestrator) {
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
}
