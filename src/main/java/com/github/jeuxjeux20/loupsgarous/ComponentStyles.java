package com.github.jeuxjeux20.loupsgarous;

import me.lucko.helper.text.Component;
import me.lucko.helper.text.TextComponent;
import me.lucko.helper.text.format.TextColor;

import static me.lucko.helper.text.format.TextColor.GRAY;
import static me.lucko.helper.text.format.TextDecoration.ITALIC;
import static me.lucko.helper.text.format.TextDecoration.UNDERLINE;

/**
 * Various styles for {@link TextComponent}.
 * <p>
 * They can be used wth {@link TextComponent#mergeStyle(Component)}, like in the following example:
 * <pre>
 *     component.mergeStyle(ComponentStyles.CLICKABLE)
 * </pre>
 */
public final class ComponentStyles {
    public static final TextComponent CLICKABLE = TextComponent.of("").decoration(UNDERLINE, true);
    public static final TextComponent TIP = TextComponent.of("").color(GRAY).decoration(ITALIC, true);

    private ComponentStyles() {
    }
}
