package com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;

import java.util.Objects;
import java.util.function.LongFunction;

public interface CompositionValidator {
    ImmutableSet<Problem> validate(Composition composition);

    final class Problem {
        private final Type type;
        private final String message;

        private Problem(Type type, String message) {
            this.type = Objects.requireNonNull(type);
            this.message = message;
        }

        public Type getType() {
            return type;
        }

        public String getMessage() {
            return message;
        }

        public boolean is(Type type) {
            return this.type == type;
        }

        public static Problem ruleBreaking(String message) {
            return new Problem(Type.RULE_BREAKING, message);
        }

        public static Problem impossible(String message) {
            return new Problem(Type.IMPOSSIBLE, message);
        }

        public static Problem of(Type type, String message) {
            return new Problem(type, message);
        }

        public enum Type {
            RULE_BREAKING("Contraire aux règles", ChatColor.GOLD),
            IMPOSSIBLE("Doit être corrigé", ChatColor.RED);

            private final String displayName;
            private final ChatColor color;

            Type(String displayName, ChatColor color) {
                this.displayName = displayName;
                this.color = color;
            }

            public String getDisplayName() {
                return displayName;
            }

            public ChatColor getColor() {
                return color;
            }
        }
    }

    final class Checks {
        private Checks() {
        }

        public static ImmutableSet<Problem> uniqueCard(Composition composition,
                                                       Class<? extends LGCard> cardClass,
                                                       LongFunction<? extends Problem> problemProvider) {
            long count = composition.getCards().stream().filter(card -> card.getClass() == cardClass).count();

            if (count > 1) {
                Problem problem = problemProvider.apply(count);
                return ImmutableSet.of(problem);
            } else {
                return ImmutableSet.of();
            }
        }
    }
}
