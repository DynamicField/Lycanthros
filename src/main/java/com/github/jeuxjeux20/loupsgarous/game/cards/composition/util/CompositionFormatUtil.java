package com.github.jeuxjeux20.loupsgarous.game.cards.composition.util;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import org.bukkit.ChatColor;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CompositionFormatUtil {
    private static final Collator FR_COLLATOR = Collator.getInstance(Locale.FRENCH);

    private CompositionFormatUtil() {
    }

    public static String format(Composition composition) {
        return format(composition.getCards().stream());
    }

    public static String format(Stream<LGCard> composition) {
        Map<Class<? extends LGCard>, List<LGCard>> cardsByType
                = composition.collect(Collectors.groupingBy(LGCard::getClass));

        if (cardsByType.isEmpty()) return ChatColor.GOLD.toString() + ChatColor.BOLD + "[Rien]";

        StringBuilder stringBuilder = new StringBuilder();

        cardsByType.entrySet().stream().sorted(Map.Entry.comparingByValue((o1, o2) -> {
            LGCard card1 = o1.get(0);
            LGCard card2 = o2.get(0);
            return FR_COLLATOR.compare(card1.getName(), card2.getName());
        })).forEach(entry -> {
            int count = entry.getValue().size();
            LGCard card = entry.getValue().get(0);

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
}
