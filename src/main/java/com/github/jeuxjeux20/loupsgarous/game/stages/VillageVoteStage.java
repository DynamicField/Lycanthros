package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.atmosphere.VoteStructure;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.VillageVoteKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

@MajorityVoteShortensCountdown
public class VillageVoteStage extends RunnableLGStage implements Votable, UnmodifiedCountdownTimedStage {
    private final VoteState currentState;
    private final Countdown unmodifiedCountdown;
    private final Countdown countdown;
    private final VoteStructure voteStructure;

    @Inject
    VillageVoteStage(@Assisted LGGameOrchestrator orchestrator, VoteStructure.Factory voteStructureFactory) {
        super(orchestrator);

        currentState = new VoteState(orchestrator, this);
        voteStructure =
                voteStructureFactory.create(orchestrator, orchestrator.world().getSpawnLocation().add(0, 2, 0), this);

        unmodifiedCountdown = Countdown.builder(90).build(orchestrator);
        countdown = Countdown.builder()
                .apply(this::addTickEvents)
                .apply(Countdown.syncWith(unmodifiedCountdown))
                .finished(this::computeVoteOutcome)
                .build(orchestrator);

        bind(currentState);
        bind(voteStructure);
        bindModule(voteStructure.createInteractionModule());
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.turn().getTime() == LGGameTurnTime.DAY;
    }

    @Override
    public CompletableFuture<Void> execute() {
        voteStructure.build();

        // Only two players? They'll vote each other and that's it.
        if (orchestrator.game().getAlivePlayers().count() <= 2) {
            unmodifiedCountdown.setTimer(30);
            countdown.setTimer(30);
            countdown.resetBiggestTimerValue();
        }

        return countdown.start();
    }

    private void computeVoteOutcome() {
        LGPlayer playerWithMostVotes = currentState.getPlayerWithMostVotes();
        if (playerWithMostVotes != null) {
            orchestrator.kills().instantly(playerWithMostVotes, VillageVoteKillReason::new);
        } else {
            orchestrator.chat().sendToEveryone(info("Le village n'a pas pu se décider !"));
        }
    }

    @Override
    public VoteState getCurrentState() {
        return currentState;
    }

    @Override
    public String getName() {
        return "Vote du village";
    }

    @Override
    public String getTitle() {
        return "Le village va voter";
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public Countdown getUnmodifiedCountdown() {
        return unmodifiedCountdown;
    }

    @Override
    public String getIndicator() {
        return "vote pour tuer";
    }
}
