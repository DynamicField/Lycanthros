package com.github.jeuxjeux20.loupsgarous.cards.composition.validation.annotations;

import com.github.jeuxjeux20.loupsgarous.cards.composition.validation.CompositionValidator.Problem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Unique {
    Problem.Type value() default Problem.Type.RULE_BREAKING;
}
