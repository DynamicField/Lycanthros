package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public class EmptyPhaseProgram extends PhaseProgram {
    public EmptyPhaseProgram(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected void startProgram() {
    }

    @Override
    protected void stopProgram() {
    }
}
