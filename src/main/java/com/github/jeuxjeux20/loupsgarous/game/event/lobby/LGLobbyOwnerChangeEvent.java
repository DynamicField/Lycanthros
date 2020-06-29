package com.github.jeuxjeux20.loupsgarous.game.event.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGLobbyOwnerChangeEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final OfflinePlayer oldOwner;
    private final LGPlayer newOwner;

    public LGLobbyOwnerChangeEvent(LGGameOrchestrator orchestrator, OfflinePlayer oldOwner, LGPlayer newOwner) {
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

    public OfflinePlayer getOldOwner() {
        return oldOwner;
    }

    public LGPlayer getNewOwner() {
        return newOwner;
    }
}
