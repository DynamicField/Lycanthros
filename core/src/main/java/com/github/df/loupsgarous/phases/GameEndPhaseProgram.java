package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.game.LGGameOrchestrator;

public class GameEndPhaseProgram extends PhaseProgram {
    public GameEndPhaseProgram(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected void startProgram() {
        getPhaseRunner().run(new GameEndPhase(orchestrator));
    }

    @Override
    protected void stopProgram() {
    }
}
