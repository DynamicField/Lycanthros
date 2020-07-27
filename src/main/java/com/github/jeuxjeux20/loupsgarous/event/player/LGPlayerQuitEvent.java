package com.github.jeuxjeux20.loupsgarous.event.player;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LGPlayerQuitEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final UUID playerUUID;
    private final LGPlayer lgPlayer;

    public LGPlayerQuitEvent(LGGameOrchestrator orchestrator, UUID playerUUID, LGPlayer lgPlayer) {
        super(orchestrator);
        this.playerUUID = playerUUID;
        this.lgPlayer = lgPlayer;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public LGPlayer getLGPlayer() {
        return lgPlayer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
