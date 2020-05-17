package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.game.LGGameOrchestrator;

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
