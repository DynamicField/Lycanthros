package com.github.jeuxjeux20.loupsgarous.phases;

public interface TimedPhase extends LGPhase {
    int getSecondsLeft();

    int getTotalSeconds();
}
