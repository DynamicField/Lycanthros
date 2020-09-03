package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.atmosphere.VoteStructure;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.Interactable;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.AbstractPlayerVote;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.kill.causes.VillageVoteKillCause;
import com.google.inject.Inject;
import org.bukkit.ChatColor;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.info;

@MajorityVoteShortensCountdown(LGInteractableKeys.Names.PLAYER_VOTE)
@PhaseInfo(
        name = "Vote du village",
        title = "Le village va voter."
)
public final class VillageVotePhase extends CountdownLGPhase {
    private final VillageVote vote;
    private final VoteStructure voteStructure;

    @Inject
    VillageVotePhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        this.vote = Interactable.createBound(VillageVote::new, LGInteractableKeys.PLAYER_VOTE, this);

        this.voteStructure = new VoteStructure(
                orchestrator,
                orchestrator.getWorld().getSpawnLocation(),
                this.vote
        );

        bind(voteStructure);
        bindModule(voteStructure.createInteractionModule());
    }

    @Override
    protected Countdown createCountdown() {
        if (orchestrator.getAlivePlayers().count() <= 2) {
            // Only two players? They'll vote each other and that's it.
            return Countdown.of(30);
        } else {
            return Countdown.of(90);
        }
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.getTurn().getTime() == LGGameTurnTime.DAY &&
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

    public static final class VillageVote extends AbstractPlayerVote {
        public VillageVote(LGGameOrchestrator orchestrator) {
            super(orchestrator);
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
