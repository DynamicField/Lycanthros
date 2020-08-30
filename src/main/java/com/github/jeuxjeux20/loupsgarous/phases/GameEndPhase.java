package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;

@PhaseInfo(name = "Fin !", color = PhaseColor.YELLOW)
public final class GameEndPhase extends CountdownLGPhase {
    @Inject
    GameEndPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(15);
    }
}
