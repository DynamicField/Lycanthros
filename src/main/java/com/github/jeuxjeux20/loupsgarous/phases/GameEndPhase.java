package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

@PhaseInfo(name = "Fin !", color = PhaseColor.YELLOW)
public final class GameEndPhase extends CountdownLGPhase {
    public GameEndPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(15);
    }

    @Override
    protected void finish() {
        orchestrator.delete();
    }
}
