package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public interface LGCard extends LGCardMetadata {
    /**
     * Gets the teams that this card has.
     * <p>
     * Those may change, depending on the events of the game.
     *
     * @return an immutable set of teams
     */
    ImmutableSet<String> getTeams();

    /**
     * Gets the tags that this card has
     * <p>
     * Those may change, depending on the events of the game.
     *
     * @return an immutable set of tags
     */
    ImmutableSet<String> getTags();

}
