package com.github.df.loupsgarous.mechanic;

import com.github.df.loupsgarous.game.LGGameOrchestrator;

public class MechanicRequest {
    private final LGGameOrchestrator orchestrator;

    public MechanicRequest(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public final LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }
}
