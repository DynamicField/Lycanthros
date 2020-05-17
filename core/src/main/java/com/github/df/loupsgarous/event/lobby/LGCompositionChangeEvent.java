package com.github.df.loupsgarous.event.lobby;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.event.LGEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGCompositionChangeEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();

    public LGCompositionChangeEvent(LGGameOrchestrator orchestrator) {
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
