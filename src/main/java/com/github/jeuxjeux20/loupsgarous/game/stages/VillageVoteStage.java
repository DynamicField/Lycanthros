package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.atmosphere.VoteStructure;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.AbstractPlayerVotable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.Votable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.VillageVoteKillReason;
import com.google.inject.Inject;
import org.bukkit.ChatColor;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

@MajorityVoteShortensCountdown(LGInteractableKeys.Names.PLAYER_VOTE)
@StageInfo(
        name = "Vote du village",
        title = "Le village va voter."
)
public class VillageVoteStage extends CountdownLGStage {
    private final VillageVotable votable;
    private final VoteStructure voteStructure;

    @Inject
    VillageVoteStage(LGGameOrchestrator orchestrator,
                     VoteStructure.Factory voteStructureFactory,
                     VillageVotable votable) {
        super(orchestrator);

        this.votable = votable;
        this.voteStructure =
                voteStructureFactory.create(orchestrator, orchestrator.world().getSpawnLocation(), votable);

        registerInteractable(votable);

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
               votable.canSomeonePick();
    }

    @Override
    protected void start() {
        voteStructure.build();
    }

    @Override
    protected void finish() {
        votable.conclude();
    }

    public VillageVotable votes() {
        return votable;
    }

    @OrchestratorScoped
    public static class VillageVotable extends AbstractPlayerVotable {
        @Inject
        VillageVotable(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        protected boolean conclude(VoteOutcome<LGPlayer> outcome) {
            Optional<LGPlayer> maybeMajority = outcome.getElected();

            if (maybeMajority.isPresent()) {
                orchestrator.kills().instantly(maybeMajority.get(), VillageVoteKillReason.INSTANCE);
            } else {
                orchestrator.chat().sendToEveryone(info("Le village n'a pas pu se décider !"));
            }

            return maybeMajority.isPresent();
        }

        @Override
        public InteractableEntry<Votable<LGPlayer>> getEntry() {
            return new InteractableEntry<>(LGInteractableKeys.PLAYER_VOTE, this);
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
