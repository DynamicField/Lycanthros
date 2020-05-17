package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.Countdown;
import com.github.df.loupsgarous.game.DeleteGameTransition;
import com.github.df.loupsgarous.game.LGGameOrchestrator;

@PhaseInfo(name = "Fin !", color = PhaseColor.YELLOW)
public final class GameEndPhase extends CountdownPhase {
    public GameEndPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(15);
    }

    @Override
    protected void finish() {
        orchestrator.stateTransitions().requestExecutionOverride(new DeleteGameTransition());
    }
}
