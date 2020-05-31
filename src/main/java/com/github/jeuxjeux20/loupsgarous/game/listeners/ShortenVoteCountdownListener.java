package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.events.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.DualCountdownStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGGameStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.MajorityVoteShortensCountdown;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShortenVoteCountdownListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onLGPick(LGPickEvent event) {
        updateStageCountdown(event.getOrchestrator().stages().current());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLGPickRemoved(LGPickRemovedEvent event) {
        updateStageCountdown(event.getOrchestrator().stages().current());
    }

    private void updateStageCountdown(LGGameStage stage) {
        if (stage instanceof DualCountdownStage && stage instanceof Votable) {
            MajorityVoteShortensCountdown annotation = stage.getClass().getAnnotation(MajorityVoteShortensCountdown.class);
            if (annotation == null) return;

            Votable votable = (Votable) stage;
            DualCountdownStage dualCountdown = (DualCountdownStage) stage;

            // Nothing will change anyway, we're under the time left.
            if (dualCountdown.getUnmodifiedCountdown().getTimer() <= annotation.timeLeft()) return;

            if (shouldShorten(annotation, votable)) {
                shortenTime(annotation, dualCountdown);
            }
            else {
                cancelShortenedTime(annotation, dualCountdown);
            }
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

        int percentage = (int)(((float)playerVoteCount / totalVoteCount) * 100);

        // For example, we have a majority percentage of 75%,
        // the player has 9 votes and there are 12 votes in total:
        // (9/12) * 100 = 75.
        // 75 >= 75 (obviously). So it should shorten the time.
        return percentage >= annotation.majorityPercentage();
    }

    private void shortenTime(MajorityVoteShortensCountdown annotation, DualCountdownStage dualCountdown) {
        // The unmodified countdown represents the real time without any modification.
        int timeLeft = Math.min(annotation.timeLeft(), dualCountdown.getUnmodifiedCountdown().getTimer());

        // So we shorten this one.
        dualCountdown.getCountdown().setTimer(timeLeft);
    }

    private void cancelShortenedTime(MajorityVoteShortensCountdown annotation, DualCountdownStage dualCountdown) {
        int unmodifiedTime = dualCountdown.getUnmodifiedCountdown().getTimer();

        dualCountdown.getCountdown().setTimer(unmodifiedTime);
    }
}
