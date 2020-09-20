package com.github.jeuxjeux20.loupsgarous.phases;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

public enum PhaseColor {
    DEFAULT(null, null),
    PINK(BarColor.PINK, ChatColor.LIGHT_PURPLE),
    BLUE(BarColor.BLUE, ChatColor.BLUE),
    RED(BarColor.RED, ChatColor.RED),
    GREEN(BarColor.GREEN, ChatColor.GREEN),
    YELLOW(BarColor.YELLOW, ChatColor.YELLOW),
    PURPLE(BarColor.PURPLE, ChatColor.DARK_PURPLE),
    WHITE(BarColor.WHITE, ChatColor.WHITE);

    private final BarColor barColor;
    private final ChatColor chatColor;

    PhaseColor(BarColor barColor, ChatColor chatColor) {
        this.barColor = barColor;
        this.chatColor = chatColor;
    }

    public BarColor toBarColor(BarColor fallback) {
        return barColor == null ? fallback : barColor;
    }

    public ChatColor toChatColor(ChatColor fallback) {
        return chatColor == null ? fallback : chatColor;
    }
}
