package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.AbstractPlayerVotable;
import org.checkerframework.common.value.qual.IntRange;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When annotated on a {@link LGStage} implementing {@link CountdownTimedStage} and {@link Votable},
 * changes the timer of the {@linkplain CountdownTimedStage#getCountdown() countdown} to
 * {@link #timeLeft()} when {@linkplain AbstractPlayerVotable#getMajorityTarget() the player with the most votes}
 * holds the same or more vote share than the {@link #majorityPercentage()}.
 * <p>
 * However, if some votes change and the majority becomes invalid, the
 * {@linkplain CountdownTimedStage#getCountdown() countdown}'s timer reverts back
 * to its initial timer, using {@link Countdown#getStartSnapshot()}.
 * <p>
 * <b>Note:</b> This doesn't apply if the initial timer is lower than {@link #timeLeft()}.
 * <p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MajorityVoteShortensCountdown {
    @IntRange(from = 0, to = 100)
    int majorityPercentage() default 60;

    int timeLeft() default 30;
}
