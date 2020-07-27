package com.github.jeuxjeux20.loupsgarous.event.stage;

import com.github.jeuxjeux20.loupsgarous.stages.LGStage;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGStageEndingEvent extends LGStageEvent {
    private static final HandlerList handlerList = new HandlerList();

    public LGStageEndingEvent(LGStage stage) {
        super(stage);
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
