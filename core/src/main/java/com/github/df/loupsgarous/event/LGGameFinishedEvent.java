package com.github.df.loupsgarous.event;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.endings.LGEnding;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGGameFinishedEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final LGEnding ending;

    public LGGameFinishedEvent(LGGameOrchestrator orchestrator, LGEnding ending) {
        super(orchestrator);
        this.ending = ending;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public LGEnding getEnding() {
        return ending;
    }
}
