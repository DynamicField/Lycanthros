package com.github.jeuxjeux20.loupsgarous.game.event.stage;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGStageChangingEvent extends LGEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final LGStage stage;
    private boolean isCancelled;

    public LGStageChangingEvent(LGGameOrchestrator orchestrator, LGStage stage) {
        super(orchestrator);
        this.stage = stage;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public LGStage getStage() {
        return stage;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}