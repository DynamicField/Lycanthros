package com.github.jeuxjeux20.loupsgarous.game.stages;

public interface TimedStage extends LGGameStage {
    int getSecondsLeft();

    int getTotalSeconds();
}
