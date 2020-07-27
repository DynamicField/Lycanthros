package com.github.jeuxjeux20.loupsgarous.cards.composition.util;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import org.bukkit.ChatColor;

import java.text.Collator;
import java.util.Locale;

public final class CompositionFormatUtil {
    private static final Collator FR_COLLATOR = Collator.getInstance(Locale.FRENCH);

    private CompositionFormatUtil() {
    }

    public static String format(Composition composition) {
        ImmutableMultiset<LGCard> contents = composition.getContents();

        if (contents.isEmpty()) return ChatColor.GOLD.toString() + ChatColor.BOLD + "[Rien]";

        StringBuilder stringBuilder = new StringBuilder();

        contents.entrySet().stream()
                .sorted(CompositionFormatUtil::compareByName)
                .forEach(entry -> {
                    int count = entry.getCount();
                    LGCard card = entry.getElement();

                    stringBuilder.append(card.getColor())
                            .append(card.getName());

                    if (count > 1) {
                        stringBuilder.append(" \u00D7 ") // Multiplication sign
                                .append(count);
                    }
                    stringBuilder.append('\n');
                });
        stringBuilder.deleteCharAt(stringBuilder.length() - 1); // Remove the last new line
        return stringBuilder.toString();
    }

    private static int compareByName(Multiset.Entry<LGCard> o1, Multiset.Entry<LGCard> o2) {
        return FR_COLLATOR.compare(o1.getElement().getName(), o2.getElement().getName());
    }
}
