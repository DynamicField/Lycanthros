package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.Plugin;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.CountdownTimedStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.MajorityVoteShortensCountdown;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public class ShortenVoteCountdownListener implements Listener {
    private final Logger logger;

    @Inject
    ShortenVoteCountdownListener(@Plugin Logger logger) {
        this.logger = logger;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLGPick(LGPickEvent event) {
        updateStageCountdown(event.getOrchestrator().stages().current());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLGPickRemoved(LGPickRemovedEvent event) {
        updateStageCountdown(event.getOrchestrator().stages().current());
    }

    private void updateStageCountdown(LGStage stage) {
        Votable votable = stage.getComponent(Votable.class).orElse(null);
        CountdownTimedStage countdownStage = stage.getComponent(CountdownTimedStage.class).orElse(null);
        MajorityVoteShortensCountdown annotation = stage.getClass().getAnnotation(MajorityVoteShortensCountdown.class);

        if (votable == null || countdownStage == null) {
            if (annotation != null) {
                logger.warning("Stage " + stage.getClass().getName() + " is annotated with " +
                               "MajorityVoteShortensCountdown but doesn't implement Votable and " +
                               "CountdownTimedStage");
            }
            return;
        }

        if (annotation == null) return;

        // Nothing will change anyway, we're under the time left.
        if (countdownStage.getCountdown().getStartSnapshot().getTimerNow() <= annotation.timeLeft()) return;

        if (shouldShorten(annotation, votable)) {
            shortenTime(annotation, countdownStage);
        } else {
            cancelShortenedTime(countdownStage);
        }
    }

    private boolean shouldShorten(MajorityVoteShortensCountdown annotation, Votable votable) {
        Votable.VoteState state = votable.getCurrentState();

        LGPlayer playerWithMostVotes = state.getPlayerWithMostVotes();
        if (playerWithMostVotes == null) {
            // Then there is no majority.
            return false;
        }
        int playerVoteCount = state.getPlayersVoteCount().get(playerWithMostVotes);
        int totalVoteCount = state.getTotalVoteCount();

        int percentage = (int) (((float) playerVoteCount / totalVoteCount) * 100);

        // For example, we have a majority percentage of 75%,
        // the player has 9 votes and there are 12 votes in total:
        // (9/12) * 100 = 75.
        // 75 >= 75 (obviously). So it should shorten the time.
        return percentage >= annotation.majorityPercentage();
    }

    private void shortenTime(MajorityVoteShortensCountdown annotation, CountdownTimedStage stage) {
        // The unmodified countdown represents the real time without any modification.
        int timeLeft = Math.min(annotation.timeLeft(), stage.getCountdown().getStartSnapshot().getTimerNow());

        // So we shorten this one.
        stage.getCountdown().setTimer(timeLeft);
    }

    private void cancelShortenedTime(CountdownTimedStage stage) {
        int unmodifiedTime = stage.getCountdown().getStartSnapshot().getTimerNow();

        stage.getCountdown().setTimer(unmodifiedTime);
    }
}
