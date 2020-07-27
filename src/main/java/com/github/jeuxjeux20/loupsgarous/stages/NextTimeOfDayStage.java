package com.github.jeuxjeux20.loupsgarous.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;

public final class NextTimeOfDayStage extends LogicLGStage {
    @Inject
    NextTimeOfDayStage(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public void start() {
        orchestrator.nextTimeOfDay();
    }
}
