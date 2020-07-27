package com.github.jeuxjeux20.loupsgarous.teams;

import org.bukkit.ChatColor;

public final class CoupleTeam extends LGTeam {
    public CoupleTeam() {
    }

    @Override
    public String getName() {
        return "En couple";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.LIGHT_PURPLE;
    }
}
