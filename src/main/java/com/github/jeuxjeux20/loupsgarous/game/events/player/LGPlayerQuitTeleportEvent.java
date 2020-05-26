package com.github.jeuxjeux20.loupsgarous.game.events.player;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LGPlayerQuitTeleportEvent extends LGEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final @Nullable LGPlayer lgPlayer;
    private final Player player;

    private boolean cancelled;

    public LGPlayerQuitTeleportEvent(LGGameOrchestrator orchestrator, @Nullable LGPlayer lgPlayer, Player player) {
        super(orchestrator);
        this.lgPlayer = lgPlayer;
        this.player = player;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public @Nullable LGPlayer getLGPlayer() {
        return lgPlayer;
    }

    public Player getPlayer() {
        return player;
    }
}
