package com.github.df.loupsgarous.kill.causes;

import com.github.df.loupsgarous.game.LGPlayer;

import java.util.Set;

public abstract class LGKillCause {
    /**
     * Gets the message displayed when the specified players die.
     *
     * @param players the players
     * @return the message to show
     */
    public abstract String getKillMessage(Set<LGPlayer> players);
}
