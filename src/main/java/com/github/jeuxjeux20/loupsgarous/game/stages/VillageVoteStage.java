package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.atmosphere.VoteStructure;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.AbstractPlayerVote;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.outcome.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.VillageVoteKillCause;
import com.google.inject.Inject;
import org.bukkit.ChatColor;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

@MajorityVoteShortensCountdown(LGInteractableKeys.Names.PLAYER_VOTE)
@StageInfo(
        name = "Vote du village",
        title = "Le village va voter."
)
public final class VillageVoteStage extends CountdownLGStage {
    private final VillageVote vote;
    private final VoteStructure voteStructure;

    @Inject
    VillageVoteStage(LGGameOrchestrator orchestrator,
                     VoteStructure.Factory voteStructureFactory,
                     InteractableRegisterer<VillageVote> vote) {
        super(orchestrator);

        this.vote = vote.as(LGInteractableKeys.PLAYER_VOTE).boundWith(this);

        this.voteStructure = voteStructureFactory.create(
                orchestrator,
                orchestrator.world().getSpawnLocation(),
                this.vote
        );

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
        return orchestrator.game().getTurn().getTime() == LGGameTurnTime.DAY &&
               vote.canSomeonePick();
    }

    @Override
    protected void start() {
        voteStructure.build();
    }

    @Override
    protected void finish() {
        vote.conclude();
    }

    public VillageVote votes() {
        return vote;
    }

    @OrchestratorScoped
    public static final class VillageVote extends AbstractPlayerVote {
        @Inject
        VillageVote(LGGameOrchestrator orchestrator, Dependencies dependencies) {
            super(orchestrator, dependencies);
        }

        @Override
        protected boolean conclude(VoteOutcome<LGPlayer> outcome) {
            Optional<LGPlayer> maybeMajority = outcome.getElected();

            if (maybeMajority.isPresent()) {
                maybeMajority.get().die(VillageVoteKillCause.INSTANCE);
            } else {
                orchestrator.chat().sendToEveryone(info("Le village n'a pas pu se décider !"));
            }

            return maybeMajority.isPresent();
        }

        @Override
        protected PickConditions<LGPlayer> additionalVoteConditions() {
            return PickConditions.empty();
        }

        @Override
        public String getPointingText() {
            return "vote pour tuer";
        }

        @Override
        public ChatColor getHighlightColor() {
            return ChatColor.RED;
        }
    }
}
