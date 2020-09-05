package com.github.jeuxjeux20.loupsgarous.phases.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.phases.LobbyPhase;
import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;

public class LobbyPhaseOverride implements PhaseOverride {
    @Override
    public boolean shouldOverride(LGGameOrchestrator orchestrator) {
        return orchestrator.getState() == LGGameState.LOBBY;
    }

    @Override
    public Class<? extends RunnableLGPhase> getPhaseClass() {
        return LobbyPhase.class;
    }

    @Override
    public RunnableLGPhase.Factory<?> getPhaseFactory() {
        return LobbyPhase::new;
    }
}
