package com.github.jeuxjeux20.loupsgarous.phases;

public interface TimedPhase extends Phase {
    int getSecondsLeft();

    int getTotalSeconds();
}
