package com.github.jeuxjeux20.loupsgarous.mechanic;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public class MechanicRequest {
    private final LGGameOrchestrator orchestrator;

    public MechanicRequest(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public final LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }
}
