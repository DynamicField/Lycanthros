package com.github.jeuxjeux20.loupsgarous.game.event.stage;

import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class LGStageEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final LGStage stage;

    public LGStageEvent(LGStage stage) {
        super(stage.gameOrchestrator());
        this.stage = stage;
    }

    public LGStage getStage() {
        return stage;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
