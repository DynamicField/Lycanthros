package com.github.jeuxjeux20.loupsgarous.game.tags;

import org.bukkit.ChatColor;

public final class MaireTag extends LGTag {
    public static final MaireTag INSTANCE = new MaireTag();

    private MaireTag() {
    }

    @Override
    public String getName() {
        return "Maire";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GOLD;
    }
}
