package com.github.jeuxjeux20.loupsgarous.game;

import org.bukkit.ChatColor;

public final class LGTeam {
    private final String name;
    private final ChatColor color;

    public LGTeam(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }
}
