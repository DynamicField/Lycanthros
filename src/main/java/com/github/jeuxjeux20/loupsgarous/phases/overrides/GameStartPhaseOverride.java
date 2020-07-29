package com.github.jeuxjeux20.loupsgarous.phases.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.phases.GameStartPhase;
import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

public class GameStartPhaseOverride extends AbstractPhaseOverride<GameStartPhase> {
    @Inject
    GameStartPhaseOverride(RunnableLGPhase.Factory<GameStartPhase> factory, TypeLiteral<GameStartPhase> typeLiteral) {
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
