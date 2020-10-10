package com.github.jeuxjeux20.loupsgarous.teams;

import com.github.jeuxjeux20.loupsgarous.mechanic.RevelationRequest;
import org.bukkit.ChatColor;

public final class LoupsGarousTeam extends LGTeam {
    public static final LoupsGarousTeam INSTANCE = new LoupsGarousTeam();

    private LoupsGarousTeam() {
    }

    @Override
    public String getName() {
        return "Loups-garous";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.RED;
    }

    @Override
    protected boolean isRevealed(RevelationRequest<LGTeam> request) {
        return request.getViewer().teams().has(this);
    }
}
