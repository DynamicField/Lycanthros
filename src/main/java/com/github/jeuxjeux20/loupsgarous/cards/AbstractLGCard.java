package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;

/**
 * The base class for all Loups-Garous cards.
 */
public abstract class AbstractLGCard implements LGCard {
    @Override
    public ImmutableSet<LGTeam> getTeams() {
        return ImmutableSet.of(getMainTeam());
    }

    protected abstract LGTeam getMainTeam();

    @Override
    public ChatColor getColor() {
        return getMainTeam().getColor();
    }

    @Override
    public String toString() {
        return getName();
    }
}
