package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import org.checkerframework.common.value.qual.IntRange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When annotated on a {@link Phase} implementing {@link CountdownTimedPhase} with the first votable
 * of the specified key name ({@link #value()}),
 * changes the timer of the countdown to
 * {@link #timeLeft()} when the candidate having a relative majority
 * holds the same or more vote share than the {@link #majorityPercentage()}.
 * <p>
 * However, if some votes change and the majority becomes invalid, the
 * countdown's timer reverts back
 * to its initial timer, using {@link Countdown#getStartSnapshot()}.
 * <p>
 * <b>Note:</b> This doesn't apply if the initial timer is lower than {@link #timeLeft()}.
 * <p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MajorityVoteShortensCountdown {
    String value();

    @IntRange(from = 0, to = 100)
    int majorityPercentage() default 60;

    int timeLeft() default 30;
}
