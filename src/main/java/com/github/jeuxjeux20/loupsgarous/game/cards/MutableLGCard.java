package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
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
    protected final ChatColor LOUPS_GAROUS_COLOR = ChatColor.RED;
    protected final ChatColor VILLAGEOIS_COLOR = ChatColor.DARK_AQUA;
    protected final ChatColor GRAY_AREA_COLOR = ChatColor.DARK_PURPLE;
    protected final Set<String> teams = new HashSet<>();
    protected final Set<String> tags = new HashSet<>();

    public MutableLGCard() {
        teams.add(getMainTeam());
    }

    @Override
    public final ImmutableSet<String> getTeams() {
        return ImmutableSet.copyOf(teams);
    }

    protected abstract String getMainTeam();

    public final Set<String> getMutableTeams() {
        return teams;
    }

    @Override
    public ImmutableSet<String> getTags() {
        return ImmutableSet.copyOf(tags);
    }

    public final Set<String> getMutableTags() {
        return tags;
    }

    @Override
    public ChatColor getColor() {
        return getMainTeam().equals(LGTeams.VILLAGEOIS) ? VILLAGEOIS_COLOR :
                getMainTeam().equals(LGTeams.LOUPS_GAROUS) ? LOUPS_GAROUS_COLOR :
                        GRAY_AREA_COLOR;
    }

    @Override
    public String toString() {
        return getName();
    }
}
