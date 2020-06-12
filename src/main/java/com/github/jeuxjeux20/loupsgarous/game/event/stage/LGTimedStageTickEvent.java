package com.github.jeuxjeux20.loupsgarous.game.event.stage;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.TimedStage;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LGTimedStageTickEvent extends LGEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final int secondsLeft;
    private final int totalSeconds;
    private final TimedStage stage;

    public LGTimedStageTickEvent(LGGameOrchestrator orchestrator, TimedStage stage) {
        super(orchestrator);
        this.secondsLeft = stage.getSecondsLeft();
        this.totalSeconds = stage.getTotalSeconds();
        this.stage = stage;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public TimedStage getStage() {
        return stage;
    }
}
