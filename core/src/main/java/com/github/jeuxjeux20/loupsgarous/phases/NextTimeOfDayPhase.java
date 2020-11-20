package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public final class NextTimeOfDayPhase extends LogicPhase {
    public NextTimeOfDayPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public void start() {
        orchestrator.nextTimeOfDay();
    }
}
