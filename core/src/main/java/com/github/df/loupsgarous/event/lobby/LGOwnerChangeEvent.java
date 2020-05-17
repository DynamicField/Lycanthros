package com.github.df.loupsgarous.event.lobby;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.event.LGEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGOwnerChangeEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final LGPlayer owner;

    public LGOwnerChangeEvent(LGGameOrchestrator orchestrator, LGPlayer owner) {
        super(orchestrator);
        this.owner = owner;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public LGPlayer getOwner() {
        return owner;
    }
}
