package com.github.jeuxjeux20.loupsgarous.event.stage;

import com.github.jeuxjeux20.loupsgarous.stages.LGStage;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGStageStartingEvent extends LGStageEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private boolean isCancelled;

    public LGStageStartingEvent(LGStage stage) {
        super(stage);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
