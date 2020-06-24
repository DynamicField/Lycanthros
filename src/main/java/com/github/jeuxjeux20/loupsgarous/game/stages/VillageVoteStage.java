package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.atmosphere.VoteStructure;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.VillageVoteKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.AbstractPlayerVotable;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

@MajorityVoteShortensCountdown
public class VillageVoteStage extends CountdownLGStage {
    private final VillageVotable votable;
    private final VoteStructure voteStructure;

    @Inject
    VillageVoteStage(@Assisted LGGameOrchestrator orchestrator, VoteStructure.Factory voteStructureFactory) {
        super(orchestrator);

        votable = new VillageVotable(orchestrator);
        voteStructure =
                voteStructureFactory.create(orchestrator, orchestrator.world().getSpawnLocation(), votable);

        bind(votable);
        bind(voteStructure);
        bindModule(voteStructure.createInteractionModule());
    }

    @Override
    protected Countdown createCountdown() {
        if (orchestrator.game().getAlivePlayers().count() <= 2) {
            // Only two players? They'll vote each other and that's it.
            return Countdown.of(30);
        } else {
            return Countdown.of(90);
        }
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.turn().getTime() == LGGameTurnTime.DAY;
    }

    @Override
    protected void start() {
        voteStructure.build();
    }

    @Override
    protected void finish() {
        votable.close();
        computeVoteOutcome();
    }

    @Override
    public String getName() {
        return "Vote du village";
    }

    @Override
    public String getTitle() {
        return "Le village va voter";
    }

    private void computeVoteOutcome() {
        LGPlayer playerWithMostVotes = votable.getMajorityTarget();
        if (playerWithMostVotes != null) {
            orchestrator.kills().instantly(playerWithMostVotes, VillageVoteKillReason::new);
        } else {
            orchestrator.chat().sendToEveryone(info("Le village n'a pas pu se décider !"));
        }
    }

    public VillageVotable votes() {
        return votable;
    }

    @Override
    public Iterable<?> getAllComponents() {
        return ImmutableList.of(votable);
    }

    public static final class VillageVotable extends AbstractPlayerVotable {
        private VillageVotable(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        public String getIndicator() {
            return "vote pour tuer";
        }
    }
}
