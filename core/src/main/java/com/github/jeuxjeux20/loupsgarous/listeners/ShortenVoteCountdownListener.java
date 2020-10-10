package com.github.jeuxjeux20.loupsgarous.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.Vote;
import com.github.jeuxjeux20.loupsgarous.phases.CountdownTimedPhase;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.MajorityVoteShortensCountdown;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShortenVoteCountdownListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onLGPick(LGPickEvent event) {
        updatePhaseCountdown(event.getOrchestrator().phases().current());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLGPickRemoved(LGPickRemovedEvent event) {
        updatePhaseCountdown(event.getOrchestrator().phases().current());
    }

    private void updatePhaseCountdown(LGPhase phase) {
        LGGameOrchestrator orchestrator = phase.getOrchestrator();

        MajorityVoteShortensCountdown annotation = phase.getClass().getAnnotation(MajorityVoteShortensCountdown.class);
        if (annotation == null) return;

        CountdownTimedPhase countdownPhase = phase.safeCast(CountdownTimedPhase.class).orElse(null);

        if (countdownPhase == null) {
            orchestrator.logger().warning("MajorityVoteShortensCountdown: " +
                                          "Phase " + phase.getClass().getName() + " is annotated with " +
                                          "MajorityVoteShortensCountdown but doesn't implement " +
                                          "CountdownTimedPhase");
            return;
        }

        Vote<?> vote = orchestrator.interactables().findKey(annotation.value())
                .flatMap(key -> key.cast(Vote.class))
                .flatMap(key -> orchestrator.interactables().single(key).getOptional())
                .orElse(null);

        if (vote == null) {
            orchestrator.logger().warning("MajorityVoteShortensCountdown: No vote with a key named "
                                          + annotation.value() + " found.");
            return;
        }

        // Nothing will change anyway, we're under the time left.
        if (countdownPhase.getCountdown().getStartSnapshot().getTimerNow() <= annotation.timeLeft()) return;

        if (shouldShorten(annotation, vote)) {
            shortenTime(annotation, countdownPhase);
        } else {
            cancelShortenedTime(countdownPhase);
        }
    }

    private boolean shouldShorten(MajorityVoteShortensCountdown annotation, Vote<?> vote) {
        Object elected = vote.getOutcome().getElected().orElse(null);
        if (elected == null) {
            return false;
        }

        int playerVoteCount = vote.getVotes().count(elected);
        int totalVoteCount = vote.getVotes().size();

        int percentage = (int) (((float) playerVoteCount / totalVoteCount) * 100);

        // For example, we have a majority percentage of 75%,
        // the player has 9 votes and there are 12 votes in total:
        // (9/12) * 100 = 75.
        // 75 >= 75 (obviously). So it should shorten the time.
        return percentage >= annotation.majorityPercentage();
    }

    private void shortenTime(MajorityVoteShortensCountdown annotation, CountdownTimedPhase phase) {
        // The unmodified countdown represents the real time without any modification.
        int timeLeft = Math.min(annotation.timeLeft(), phase.getCountdown().getStartSnapshot().getTimerNow());

        // So we shorten this one.
        phase.getCountdown().setTimer(timeLeft);
    }

    private void cancelShortenedTime(CountdownTimedPhase phase) {
        int unmodifiedTime = phase.getCountdown().getStartSnapshot().getTimerNow();

        phase.getCountdown().setTimer(unmodifiedTime);
    }
}