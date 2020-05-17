package com.github.df.loupsgarous.event;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.kill.LGKill;
import com.google.common.collect.ImmutableSet;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class LGKillEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();

    private final ImmutableSet<LGKill> kills;

    public LGKillEvent(LGGameOrchestrator orchestrator, Collection<LGKill> kills) {
        super(orchestrator);
        this.kills = ImmutableSet.copyOf(kills);
    }

    public LGKillEvent(LGGameOrchestrator orchestrator, LGKill kill) {
        this(orchestrator, Collections.singletonList(kill));
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    public ImmutableSet<LGKill> getKills() {
        return kills;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
