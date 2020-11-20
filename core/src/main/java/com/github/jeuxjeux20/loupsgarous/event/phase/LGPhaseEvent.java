package com.github.jeuxjeux20.loupsgarous.event.phase;

import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.phases.Phase;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class LGPhaseEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Phase phase;

    public LGPhaseEvent(Phase phase) {
        super(phase.getOrchestrator());
        this.phase = phase;
    }

    public Phase getPhase() {
        return phase;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
