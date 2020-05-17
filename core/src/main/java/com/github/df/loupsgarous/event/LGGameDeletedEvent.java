package com.github.df.loupsgarous.event;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGGameDeletedEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();

    public LGGameDeletedEvent(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
