package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.cards.annotations.CardDescription;
import com.github.jeuxjeux20.loupsgarous.game.cards.annotations.CardName;
import com.github.jeuxjeux20.loupsgarous.game.cards.annotations.CardTeam;

import java.lang.annotation.Annotation;
import java.util.Objects;

public interface AnnotatedLGCard extends LGCard {
    @Override
    default String getName() {
        return getAnnotation(this, CardName.class).singular();
    }

    @Override
    default String getPluralName() {
        return getAnnotation(this, CardName.class).plural();
    }

    @Override
    default boolean isFeminineName() {
        return getAnnotation(this, CardName.class).isFeminine();
    }

    @Override
    default String getDescription() {
        return getAnnotation(this, CardDescription.class).value();
    }

    @Override
    default String getMainTeam() {
        return getAnnotation(this, CardTeam.class).value();
    }

    static <T extends Annotation> T getAnnotation(Object obj, Class<? extends T> clazz) {
        return Objects.requireNonNull(obj.getClass().getAnnotation(clazz),
                "No " + clazz.getSimpleName() + " annotation found on class " + clazz.getName() + ".");
    }
}
