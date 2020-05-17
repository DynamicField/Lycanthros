package com.github.df.loupsgarous.game;

public final class LGPlayerAndGame {
    private final LGPlayer player;
    private final LGGameOrchestrator orchestrator;

    public LGPlayerAndGame(LGPlayer player, LGGameOrchestrator orchestrator) {
        this.player = player;
        this.orchestrator = orchestrator;
    }

    public LGPlayer getPlayer() {
        return player;
    }

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }
}
