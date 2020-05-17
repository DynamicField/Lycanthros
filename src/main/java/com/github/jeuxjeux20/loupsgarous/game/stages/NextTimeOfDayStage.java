package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class NextTimeOfDayStage extends LogicLGGameStage {
    @Inject
    public NextTimeOfDayStage(@Assisted LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public void runSync() {
        orchestrator.nextTimeOfDay();
    }
}
