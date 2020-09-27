package com.github.jeuxjeux20.loupsgarous.chat;

import me.lucko.helper.text.TextComponent;
import me.lucko.helper.text.event.ClickEvent;
import me.lucko.helper.text.event.HoverEvent;

/**
 * Various templates for {@link TextComponent}s.
 */
public final class ComponentTemplates {
    public static final TextComponent VOTE_TIP =
            TextComponent.of("Faites ").mergeStyle(ComponentStyles.TIP)
                    .append(command("/lgvote", "<joueur>"))
                    .append(TextComponent.of(" pour voter !"));

    private ComponentTemplates() {
    }

    public static TextComponent command(String command, String placeholder) {
        if (!command.startsWith("/")) command = "/" + command;

        return TextComponent.of(command + " " + placeholder)
                .mergeStyle(ComponentStyles.CLICKABLE)
                .hoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.of("Cliquez ici pour utiliser la commande !")))
                .clickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command + " "));
    }
}
