package com.github.jeuxjeux20.loupsgarous.game.stages;

public interface TimedStage extends LGStage {
    int getSecondsLeft();

    int getTotalSeconds();
}
