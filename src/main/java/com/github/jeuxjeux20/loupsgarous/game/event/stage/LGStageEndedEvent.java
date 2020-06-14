package com.github.jeuxjeux20.loupsgarous.game.event.stage;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGStageEndedEvent extends LGStageEvent {
    private static final HandlerList handlerList = new HandlerList();

    public LGStageEndedEvent(LGGameOrchestrator orchestrator, LGStage stage) {
        super(orchestrator, stage);
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
