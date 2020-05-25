package com.github.jeuxjeux20.loupsgarous.game.events;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGLobbyPlayerQuitEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;

    public LGLobbyPlayerQuitEvent(LGGameOrchestrator orchestrator, Player player) {
        super(orchestrator);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
