package com.github.jeuxjeux20.loupsgarous.game.cards.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CardName {
    String singular();
    String plural();
    boolean isFeminine();
}
