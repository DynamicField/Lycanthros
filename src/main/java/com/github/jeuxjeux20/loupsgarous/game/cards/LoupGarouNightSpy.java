package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface LoupGarouNightSpy {
    default boolean canSpy(LGPlayer spy) {
        return spy.isAlive();
    }
}
