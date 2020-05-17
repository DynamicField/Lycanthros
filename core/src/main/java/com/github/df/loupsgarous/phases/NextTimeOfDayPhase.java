package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.game.LGGameOrchestrator;

public final class NextTimeOfDayPhase extends LogicPhase {
    public NextTimeOfDayPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public void start() {
        orchestrator.nextTimeOfDay();
    }
}
