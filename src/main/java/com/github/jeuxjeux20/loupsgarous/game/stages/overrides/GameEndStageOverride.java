package com.github.jeuxjeux20.loupsgarous.game.stages.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.stages.GameEndStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.RunnableLGGameStage;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

public class GameEndStageOverride extends AbstractStageOverride<GameEndStage> {
    @Inject
    GameEndStageOverride(RunnableLGGameStage.Factory<GameEndStage> factory, TypeLiteral<GameEndStage> typeLiteral) {
        super(factory, typeLiteral);
    }

    @Override
    public boolean shouldOverride(LGGameOrchestrator orchestrator) {
        return orchestrator.state() == LGGameState.FINISHED;
    }

    @Override
    public void onceComplete(LGGameOrchestrator orchestrator) {
        orchestrator.delete();
    }
}
