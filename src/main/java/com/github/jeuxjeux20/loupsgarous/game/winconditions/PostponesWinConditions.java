package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import java.lang.annotation.*;

/**
 * Indicates that the annotated type postpones win condition checks.
 *
 * @see WinCondition
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PostponesWinConditions {
}
