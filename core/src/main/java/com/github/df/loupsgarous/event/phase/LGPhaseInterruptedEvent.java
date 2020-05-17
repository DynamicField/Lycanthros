package com.github.df.loupsgarous.event.phase;

import com.github.df.loupsgarous.phases.Phase;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGPhaseInterruptedEvent extends LGPhaseEvent {
    private static final HandlerList handlerList = new HandlerList();

    public LGPhaseInterruptedEvent(Phase phase) {
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