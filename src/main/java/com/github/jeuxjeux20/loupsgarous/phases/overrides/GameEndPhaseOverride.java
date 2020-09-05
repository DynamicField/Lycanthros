package com.github.jeuxjeux20.loupsgarous.phases.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.phases.GameEndPhase;
import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;

public class GameEndPhaseOverride implements PhaseOverride {
    @Override
    public boolean shouldOverride(LGGameOrchestrator orchestrator) {
        return orchestrator.getState() == LGGameState.FINISHED;
    }

    @Override
    public Class<? extends RunnableLGPhase> getPhaseClass() {
        return GameEndPhase.class;
    }

    @Override
    public RunnableLGPhase.Factory<?> getPhaseFactory() {
        return GameEndPhase::new;
    }
}
