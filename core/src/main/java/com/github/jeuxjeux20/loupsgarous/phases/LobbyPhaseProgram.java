package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public class LobbyPhaseProgram extends PhaseProgram {
    public LobbyPhaseProgram(LGGameOrchestrator orchestrator) {
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
