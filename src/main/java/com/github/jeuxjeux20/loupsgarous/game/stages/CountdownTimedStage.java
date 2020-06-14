package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.event.CountdownTickEvent;

public interface CountdownTimedStage extends TimedStage {
    Countdown getCountdown();

    @Override
    default int getSecondsLeft() {
        return getCountdown() == null ? 1 : getCountdown().getTimer();
    }

    @Override
    default int getTotalSeconds() {
        return getCountdown() == null ? 1 : getCountdown().getBiggestTimerValue();
    }
}
