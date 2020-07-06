package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;

public class NextTimeOfDayStage extends LogicLGStage {
    @Inject
    NextTimeOfDayStage(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public void start() {
        orchestrator.nextTimeOfDay();
    }
}
