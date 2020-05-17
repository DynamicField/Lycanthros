package com.github.df.loupsgarous.event.extensibility;

import com.github.df.loupsgarous.extensibility.Mod;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ModEnabledEvent extends ModEvent {
    private static final HandlerList handlerList = new HandlerList();

    public ModEnabledEvent(LGGameOrchestrator orchestrator, Mod mod) {
        super(orchestrator, mod);
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}