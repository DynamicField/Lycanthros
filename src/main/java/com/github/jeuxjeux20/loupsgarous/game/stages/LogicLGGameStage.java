package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class LogicLGGameStage extends RunnableLGGameStage {
    public LogicLGGameStage(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public final CompletableFuture<Void> run() {
        runSync();
        return COMPLETED;
    }

    public abstract void runSync();

    @Override
    public boolean isLogic() {
        return true;
    }

    @Override
    public @Nullable String getName() {
        return null;
    }
}
