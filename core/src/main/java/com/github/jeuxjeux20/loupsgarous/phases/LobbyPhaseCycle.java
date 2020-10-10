package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public class LobbyPhaseCycle extends PhaseProgram {
    public LobbyPhaseCycle(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected void startProgram() {
        getPhaseRunner().run(new LobbyPhase(orchestrator));
    }

    @Override
    protected void stopProgram() {
    }
}