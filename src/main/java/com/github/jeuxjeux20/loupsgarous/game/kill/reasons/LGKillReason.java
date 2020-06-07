package com.github.jeuxjeux20.loupsgarous.game.kill.reasons;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public abstract class LGKillReason {
    /**
     * Gets the message displayed when the specified {@code player} dies.
     *
     * @param player the player
     * @return the message to show
     */
    public abstract String getKillMessage(LGPlayer player);
}
