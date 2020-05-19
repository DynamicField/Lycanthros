package com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.annotations;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator.Problem;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
    Problem.Type value() default Problem.Type.RULE_BREAKING;
}
