package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.killreasons.VillageVoteKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

public class VillageVoteStage extends AsyncLGGameStage implements Votable, CountdownTimedStage {
    private final VoteState currentState;
    private final TickEventCountdown countdown;

    @Inject
    public VillageVoteStage(@Assisted LGGameOrchestrator orchestrator) {
        super(orchestrator);

        currentState = new VoteState(orchestrator, this);
        countdown = new TickEventCountdown(this, 15);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.getTurn().getTime() == LGGameTurnTime.DAY;
    }

    @Override
    public CompletableFuture<Void> run() {
        return countdown.start().thenRun(this::computeVoteOutcome);
    }

    private void computeVoteOutcome() {
        LGPlayer playerWithMostVotes = currentState.getPlayerWithMostVotes();
        if (playerWithMostVotes != null) {
            orchestrator.killInstantly(playerWithMostVotes, VillageVoteKillReason::new);
        } else {
            orchestrator.sendToEveryone(info("Le village n'a pas pu se décider !"));
        }
    }

    @Override
    public VoteState getCurrentState() {
        return currentState;
    }

    @Override
    public @NotNull String getName() {
        return "Vote du village";
    }

    @Override
    public Optional<String> getTitle() {
        return Optional.of("Le village va voter");
    }

    @Override
    public TickEventCountdown getCountdown() {
        return countdown;
    }

    @Override
    public String getIndicator() {
        return "vote pour tuer";
    }
}
