package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

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
