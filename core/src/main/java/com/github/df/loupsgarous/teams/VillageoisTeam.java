package com.github.df.loupsgarous.teams;

import org.bukkit.ChatColor;

public final class VillageoisTeam extends LGTeam {
    public static final VillageoisTeam INSTANCE = new VillageoisTeam();

    private VillageoisTeam() {
    }

    @Override
    public String getName() {
        return "Villageois";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_AQUA;
    }
}
