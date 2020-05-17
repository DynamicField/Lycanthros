package com.github.df.loupsgarous.phases;

public interface TimedPhase extends Phase {
    int getSecondsLeft();

    int getTotalSeconds();
}
