package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Set;

/**
 * The base class for all Loups-Garous cards.
 * <p>
 * Cards are instantiated for every {@linkplain LGPlayer player}, and can contain state.
 * <p>
 * They are also used to "extend" a player's capacities.
 */
public abstract class MutableLGCard implements LGCard {
    protected final Set<LGTeam> teams = new HashSet<>();

    public MutableLGCard() {
        teams.add(getMainTeam());
    }

    @Override
    public final ImmutableSet<LGTeam> getTeams() {
        return ImmutableSet.copyOf(teams);
    }

    protected abstract LGTeam getMainTeam();

    public final Set<LGTeam> getMutableTeams() {
        return teams;
    }

    @Override
    public ChatColor getColor() {
        return getMainTeam().getColor();
    }

    @Override
    public String toString() {
        return getName();
    }
}
