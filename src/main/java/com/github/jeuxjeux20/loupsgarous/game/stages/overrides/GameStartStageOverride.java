package com.github.jeuxjeux20.loupsgarous.game.stages.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.stages.GameStartStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.RunnableLGStage;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

public class GameStartStageOverride extends AbstractStageOverride<GameStartStage> {
    @Inject
    GameStartStageOverride(RunnableLGStage.Factory<GameStartStage> factory, TypeLiteral<GameStartStage> typeLiteral) {
        super(factory, typeLiteral);
    }

    @Override
    public boolean shouldOverride(LGGameOrchestrator orchestrator) {
        return orchestrator.state() == LGGameState.WAITING_FOR_PLAYERS ||
               orchestrator.state() == LGGameState.READY_TO_START;
    }

    @Override
    public void onceComplete(LGGameOrchestrator orchestrator) {
        orchestrator.start();
    }
}
