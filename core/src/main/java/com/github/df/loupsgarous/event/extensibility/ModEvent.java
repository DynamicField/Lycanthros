package com.github.df.loupsgarous.event.extensibility;

import com.github.df.loupsgarous.event.LGEvent;
import com.github.df.loupsgarous.extensibility.Mod;
import com.github.df.loupsgarous.game.LGGameOrchestrator;

public abstract class ModEvent extends LGEvent {
    private final Mod mod;

    public ModEvent(LGGameOrchestrator orchestrator,
            Mod mod) {
        super(orchestrator);
        this.mod = mod;
    }

    public Mod getMod() {
        return mod;
    }
}