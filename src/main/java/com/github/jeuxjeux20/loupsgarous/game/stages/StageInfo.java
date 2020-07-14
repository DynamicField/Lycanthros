package com.github.jeuxjeux20.loupsgarous.game.stages;

import java.lang.annotation.*;

/**
 * Holds information about a stage.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface StageInfo {
    /**
     * Gets the name of this stage, an empty string is considered as a {@code null} value.
     * @return the name of this stage
     */
    String name() default "";

    String title() default "";

    StageColor color() default StageColor.DEFAULT;

    boolean isTemporary() default false;
}
