package com.github.jeuxjeux20.loupsgarous.stages;

public interface TimedStage extends LGStage {
    int getSecondsLeft();

    int getTotalSeconds();
}
