package com.github.jeuxjeux20.loupsgarous.game.stages.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.stages.AsyncLGGameStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStagesOrchestrator;

public interface StageOverride {
    boolean shouldOverride(LGGameOrchestrator orchestrator);

    void onceComplete(LGGameOrchestrator orchestrator);

    Class<? extends AsyncLGGameStage> getStageClass();

    AsyncLGGameStage.Factory<?> getStageFactory();
}
