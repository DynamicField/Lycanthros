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

import java.util.concurrent.CompletableFuture;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

@MajorityVoteShortensCountdown
public class VillageVoteStage extends CountdownLGStage implements Votable, UnmodifiedCountdownTimedStage {
    private final VoteState voteState;
    private final Countdown unmodifiedCountdown;
    private final VoteStructure voteStructure;

    @Inject
    VillageVoteStage(@Assisted LGGameOrchestrator orchestrator, VoteStructure.Factory voteStructureFactory) {
        super(orchestrator);

        voteState = new VoteState(orchestrator, this);
        voteStructure =
                voteStructureFactory.create(orchestrator, orchestrator.world().getSpawnLocation(), this);

        unmodifiedCountdown = Countdown.builder(90).build();

        bind(voteState);
        bind(voteStructure);
        bindModule(voteStructure.createInteractionModule());
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.builder()
                .apply(Countdown.syncWith(unmodifiedCountdown))
                .finished(voteState::close)
                .finished(this::computeVoteOutcome)
                .build();
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.turn().getTime() == LGGameTurnTime.DAY;
    }

    @Override
    protected void start() {
        voteStructure.build();

        // Only two players? They'll vote each other and that's it.
        if (orchestrator.game().getAlivePlayers().count() <= 2) {
            unmodifiedCountdown.setTimer(30);
            getCountdown().setTimer(30);
            getCountdown().resetBiggestTimerValue();
        }
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
    public String getIndicator() {
        return "vote pour tuer";
    }

    private void computeVoteOutcome() {
        LGPlayer playerWithMostVotes = voteState.getPlayerWithMostVotes();
        if (playerWithMostVotes != null) {
            orchestrator.kills().instantly(playerWithMostVotes, VillageVoteKillReason::new);
        } else {
            orchestrator.chat().sendToEveryone(info("Le village n'a pas pu se décider !"));
        }
    }

    @Override
    public VoteState getCurrentState() {
        return voteState;
    }

    @Override
    public Countdown getUnmodifiedCountdown() {
        return unmodifiedCountdown;
    }
}
