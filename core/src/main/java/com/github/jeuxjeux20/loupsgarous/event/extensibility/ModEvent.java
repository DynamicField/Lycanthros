package com.github.jeuxjeux20.loupsgarous.event.extensibility;

import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.Mod;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

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