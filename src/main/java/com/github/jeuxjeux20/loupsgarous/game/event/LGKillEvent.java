package com.github.jeuxjeux20.loupsgarous.game.event;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import com.google.common.collect.ImmutableList;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class LGKillEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();

    private final ImmutableList<LGKill> kills;

    public LGKillEvent(LGGameOrchestrator orchestrator, List<LGKill> kills) {
        super(orchestrator);
        this.kills = ImmutableList.copyOf(kills);
    }

    public LGKillEvent(LGGameOrchestrator orchestrator, LGKill kill) {
        this(orchestrator, Collections.singletonList(kill));
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    public ImmutableList<LGKill> getKills() {
        return kills;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
