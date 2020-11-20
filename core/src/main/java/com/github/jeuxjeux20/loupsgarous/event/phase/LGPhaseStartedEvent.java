package com.github.jeuxjeux20.loupsgarous.event.phase;

import com.github.jeuxjeux20.loupsgarous.phases.Phase;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGPhaseStartedEvent extends LGPhaseEvent {
    private static final HandlerList handlerList = new HandlerList();

    public LGPhaseStartedEvent(Phase phase) {
        super(phase);
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
