package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.Plugin;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableProvider;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Votable;
import com.github.jeuxjeux20.loupsgarous.game.stages.CountdownTimedStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.MajorityVoteShortensCountdown;
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
    public void onLGPick(LGPickEvent<?, ?> event) {
        updateStageCountdown(event.getOrchestrator().stages().current());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLGPickRemoved(LGPickRemovedEvent<?, ?> event) {
        updateStageCountdown(event.getOrchestrator().stages().current());
    }

    private void updateStageCountdown(LGStage stage) {
        Votable<?> votable = stage.safeCast(InteractableProvider.class)
                .flatMap(p -> p.getInteractables(Votable.class).stream()
                        .map(InteractableEntry::getValue)
                        .findAny())
                .orElse(null);
        CountdownTimedStage countdownStage = stage.safeCast(CountdownTimedStage.class).orElse(null);
        MajorityVoteShortensCountdown annotation = stage.getClass().getAnnotation(MajorityVoteShortensCountdown.class);

        if (votable == null || countdownStage == null) {
            if (annotation != null) {
                logger.warning("Stage " + stage.getClass().getName() + " is annotated with " +
                               "MajorityVoteShortensCountdown but doesn't have a Votable interactable and " +
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

    private boolean shouldShorten(MajorityVoteShortensCountdown annotation, Votable<?> votable) {
        Object majority = votable.getMajorityTarget();
        if (majority == null) {
            // The vote determines that there is no majority.
            return false;
        }
        int playerVoteCount = votable.getTargetVoteCount().get(majority);
        int totalVoteCount = votable.getTotalVoteCount();

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
