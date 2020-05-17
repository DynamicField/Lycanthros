package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public interface LGCardMetadata {
    ChatColor LOUPS_GAROUS_COLOR = ChatColor.RED;
    ChatColor VILLAGEOIS_COLOR = ChatColor.DARK_AQUA;
    ChatColor GRAY_AREA_COLOR = ChatColor.DARK_PURPLE;

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

    String getMainTeam();

    /**
     * Gets the description, shown at the start of the game, of this card.
     *
     * @return the description of this card
     */
    String getDescription();

    default ChatColor getColor() {
        return getMainTeam().equals(LGTeams.VILLAGEOIS) ? VILLAGEOIS_COLOR :
                getMainTeam().equals(LGTeams.LOUPS_GAROUS) ? LOUPS_GAROUS_COLOR :
                        GRAY_AREA_COLOR;
    }

    ItemStack createGuiItem();
}
