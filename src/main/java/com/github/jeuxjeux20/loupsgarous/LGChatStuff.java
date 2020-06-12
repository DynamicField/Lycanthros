package com.github.jeuxjeux20.loupsgarous;

import me.lucko.helper.text.TextComponent;
import me.lucko.helper.text.event.ClickEvent;
import org.bukkit.ChatColor;

public final class LGChatStuff {
    public static final char HEAL_SYMBOL = '\u271A'; // Plus: ✚
    public static final char SKULL_SYMBOL = '\u2620'; // Skull: ☠ (displayed without bones in Minecraft)
    public static final char HEART_SYMBOL = '\u2764'; // Heart: ❤
    public static final char RIGHT_ARROWHEAD_SYMBOL = '\u27A4'; // Right arrowhead: ➤
    public static final char VOYANTE_SYMBOL = '\u2742'; // This thing: ❂

    public static final String IMPORTANT_TIP_COLOR = ChatColor.LIGHT_PURPLE.toString();
    public static final String INFO_COLOR = ChatColor.AQUA.toString();
    public static final String BANNER = ChatColor.AQUA + "=======================";

    public static final TextComponent VOTE_TIP_COMPONENT =
            TextComponent.of("Faites ").mergeStyle(ComponentStyles.TIP)
                    .append(TextComponent.of("/lgvote <joueur>")
                            .mergeStyle(ComponentStyles.CLICKABLE)
                            .clickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/lgvote ")))
                    .append(TextComponent.of(" pour voter !"));

    private LGChatStuff() {
    }

    public static String banner(String text) {
        return ChatColor.AQUA + "=====[" + text + "]=====";
    }

    public static String player(String text) {
        return ChatColor.BOLD + text;
    }

    public static String role(String text) {
        return ChatColor.GOLD.toString() + ChatColor.UNDERLINE + text;
    }

    public static String vote(String text) {
        return ChatColor.LIGHT_PURPLE + text;
    }

    public static String killMessage(String text) {
        return ChatColor.RED + text;
    }

    public static String info(String text) {
        return INFO_COLOR + text;
    }

    public static String importantTip(String text) {
        return IMPORTANT_TIP_COLOR + text;
    }

    public static String error(String text) {
        return ChatColor.RED + text;
    }

    public static String lobbyMessage(String text) {
        return ChatColor.WHITE + text;
    }

    public static String slots(String text) {
        return ChatColor.AQUA + text;
    }

    public static String importantInfo(String text) {
        return ChatColor.GOLD + ChatColor.BOLD.toString() + RIGHT_ARROWHEAD_SYMBOL + ' ' + text; // Arrow
    }
}
