package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.IdentifiedAs;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

@IdentifiedAs(NextTimeOfDayPhase.IDENTIFIER)
public final class NextTimeOfDayPhase extends LogicLGPhase {
    public static final String IDENTIFIER = "NextTimeOfDay";

    public NextTimeOfDayPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public void start() {
        orchestrator.nextTimeOfDay();
    }
}
