package com.github.df.loupsgarous.event;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import org.bukkit.event.Event;

public abstract class LGEvent extends Event {
    private final LGGameOrchestrator orchestrator;

    public LGEvent(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }
}
