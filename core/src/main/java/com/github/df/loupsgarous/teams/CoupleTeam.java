package com.github.df.loupsgarous.teams;

import com.github.df.loupsgarous.mechanic.RevelationRequest;
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
    protected boolean isRevealed(RevelationRequest<LGTeam> request) {
        return request.getViewer().teams().has(this);
    }
}
