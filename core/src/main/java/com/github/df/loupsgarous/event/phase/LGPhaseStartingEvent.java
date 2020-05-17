package com.github.df.loupsgarous.event.phase;

import com.github.df.loupsgarous.phases.Phase;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGPhaseStartingEvent extends LGPhaseEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean isCancelled;

    public LGPhaseStartingEvent(Phase phase) {
        super(phase);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        if (isCancelled && !cancel && getPhase().getState() != Phase.State.PREPARING) {
            throw new IllegalStateException(
                    "Cannot set un-cancel this event when the phase has already terminated.");
        }
        isCancelled = cancel;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
