package com.github.jeuxjeux20.loupsgarous.phases.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.phases.GameStartPhase;
import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;

public class GameStartPhaseOverride implements PhaseOverride {
    @Override
    public boolean shouldOverride(LGGameOrchestrator orchestrator) {
        return orchestrator.state() == LGGameState.WAITING_FOR_PLAYERS ||
               orchestrator.state() == LGGameState.READY_TO_START;
    }

    @Override
    public void onceComplete(LGGameOrchestrator orchestrator) {
        orchestrator.start();
    }

    @Override
    public Class<? extends RunnableLGPhase> getPhaseClass() {
        return GameStartPhase.class;
    }
}
