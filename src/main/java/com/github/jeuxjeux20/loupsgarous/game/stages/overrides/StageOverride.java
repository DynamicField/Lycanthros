package com.github.jeuxjeux20.loupsgarous.game.stages.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.stages.RunnableLGGameStage;

public interface StageOverride {
    boolean shouldOverride(LGGameOrchestrator orchestrator);

    void onceComplete(LGGameOrchestrator orchestrator);

    Class<? extends RunnableLGGameStage> getStageClass();

    RunnableLGGameStage.Factory<?> getStageFactory();
}
