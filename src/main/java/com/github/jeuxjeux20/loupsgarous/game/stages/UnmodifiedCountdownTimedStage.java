package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;

public interface UnmodifiedCountdownTimedStage extends CountdownTimedStage {
    /**
     * Gets the countdown that doesn't apply any time modifications.
     *
     * @return the unmodified countdown
     */
    Countdown getUnmodifiedCountdown();
}
