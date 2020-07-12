package com.github.jeuxjeux20.loupsgarous.game.event.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGLobbyOwnerChangeEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final LGPlayer owner;

    public LGLobbyOwnerChangeEvent(LGGameOrchestrator orchestrator, LGPlayer owner) {
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
