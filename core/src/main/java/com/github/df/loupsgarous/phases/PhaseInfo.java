package com.github.df.loupsgarous.phases;

import java.lang.annotation.*;

/**
 * Holds information about a phase.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PhaseInfo {
    /**
     * Gets the name of this phase, an empty string is considered as a {@code null} value.
     * @return the name of this phase
     */
    String name() default "";

    String title() default "";

    PhaseColor color() default PhaseColor.DEFAULT;

    boolean isTemporary() default false;
}
