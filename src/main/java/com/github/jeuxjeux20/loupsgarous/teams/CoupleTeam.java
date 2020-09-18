package com.github.jeuxjeux20.loupsgarous.teams;

import org.bukkit.ChatColor;

public final class CoupleTeam extends LGTeam {
    public CoupleTeam() {
    }

    @Override
    public String getName() {
        return "Couple";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    @Override
    protected void setupRevelation(TeamRevelationContext context) {
        if (context.getViewer().teams().has(this)) {
            context.reveal();
        }
    }
}
