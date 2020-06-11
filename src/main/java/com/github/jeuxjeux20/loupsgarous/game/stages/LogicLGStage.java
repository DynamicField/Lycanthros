package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public abstract class LogicLGStage extends RunnableLGStage {
    public LogicLGStage(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public final CompletableFuture<Void> execute() {
        runSync();
        return CompletableFuture.completedFuture(null);
    }

    public abstract void runSync();

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
