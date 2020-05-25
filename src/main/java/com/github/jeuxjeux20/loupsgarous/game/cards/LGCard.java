package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.google.common.collect.ImmutableSet;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface LGCard {
    /**
     * Gets the name of this card
     *
     * @return the name of this card
     */
    String getName();

    String getPluralName();

    default String getLowercasePluralName() {
        String pluralName = getPluralName();

        if (pluralName.length() <= 1) {
            return pluralName.toLowerCase();
        }

        return pluralName.substring(0, 1).toLowerCase() + pluralName.substring(1);
    }

    boolean isFeminineName();

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

    /**
     * Gets the description, shown at the start of the game, of this card.
     *
     * @return the description of this card
     */
    String getDescription();

    ChatColor getColor();

    ItemStack createGuiItem();

    final class Unknown implements LGCard {
        @Override
        public String getName() {
            return "Inconnu";
        }

        @Override
        public String getPluralName() {
            return "Inconnus";
        }

        @Override
        public boolean isFeminineName() {
            return false;
        }

        @Override
        public ImmutableSet<String> getTeams() {
            return ImmutableSet.of();
        }

        @Override
        public ImmutableSet<String> getTags() {
            return ImmutableSet.of();
        }

        @Override
        public String getDescription() {
            return "?";
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.RESET;
        }

        @Override
        public ItemStack createGuiItem() {
            return ItemStackBuilder.of(Material.BARRIER).build();
        }
    }
}
