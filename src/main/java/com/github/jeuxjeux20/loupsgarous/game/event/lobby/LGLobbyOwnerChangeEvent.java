package com.github.jeuxjeux20.loupsgarous.game.event.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGLobbyOwnerChangeEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Player oldOwner;
    private final Player newOwner;

    public LGLobbyOwnerChangeEvent(LGGameOrchestrator orchestrator, Player oldOwner, Player newOwner) {
        super(orchestrator);
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public Player getOldOwner() {
        return oldOwner;
    }

    public Player getNewOwner() {
        return newOwner;
    }
}
