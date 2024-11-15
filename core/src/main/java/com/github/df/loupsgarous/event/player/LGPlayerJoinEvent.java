package com.github.df.loupsgarous.event.player;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.event.LGEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGPlayerJoinEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final Player player;
    private final LGPlayer lgPlayer;

    public LGPlayerJoinEvent(LGGameOrchestrator orchestrator, Player player, LGPlayer lgPlayer) {
        super(orchestrator);
        this.player = player;
        this.lgPlayer = lgPlayer;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    public Player getPlayer() {
        return player;
    }

    public LGPlayer getLGPlayer() {
        return lgPlayer;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
