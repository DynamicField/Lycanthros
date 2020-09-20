package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.ImmutableList;

public class LobbyPhaseCycle extends PhaseCycle {
    public LobbyPhaseCycle(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        setPhases(ImmutableList.of(LobbyPhase::new));
    }
}
