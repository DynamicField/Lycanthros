package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.ImmutableList;

public class GameEndPhaseCycle extends PhaseCycle {
    public GameEndPhaseCycle(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        setPhases(ImmutableList.of(GameEndPhase::new));
    }
}
