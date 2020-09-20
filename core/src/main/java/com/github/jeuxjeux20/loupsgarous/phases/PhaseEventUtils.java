package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.event.CountdownTickEvent;

public final class PhaseEventUtils {
    private PhaseEventUtils() {
    }

    public static boolean isCurrentPhaseCountdownEvent(LGGameOrchestrator orchestrator, CountdownTickEvent event) {
        LGPhase phase = orchestrator.phases().current();

        return phase instanceof CountdownTimedPhase &&
               ((CountdownTimedPhase) phase).getCountdown() == event.getCountdown();
    }
}
