package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.Countdown;
import com.github.df.loupsgarous.atmosphere.VoteStructure;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGGameTurnTime;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.LGInteractableKeys;
import com.github.df.loupsgarous.interaction.condition.PickConditions;
import com.github.df.loupsgarous.interaction.vote.PlayerVote;
import com.github.df.loupsgarous.interaction.vote.outcome.VoteOutcome;
import com.github.df.loupsgarous.kill.causes.VillageVoteKillCause;
import org.bukkit.ChatColor;

import java.util.Optional;

import static com.github.df.loupsgarous.chat.LGChatStuff.info;

@MajorityVoteShortensCountdown(LGInteractableKeys.PLAYER_VOTE)
@PhaseInfo(
        name = "Vote du village",
        title = "Le village va voter."
)
public final class VillageVotePhase extends CountdownPhase {
    private final VillageVote vote;
    private final VoteStructure voteStructure;

    public VillageVotePhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        this.vote = new VillageVote(orchestrator);

        this.voteStructure = new VoteStructure(
                orchestrator,
                orchestrator.getWorld().getSpawnLocation(),
                this.vote
        );

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
        vote.register(LGInteractableKeys.PLAYER_VOTE).bindWith(this);
        voteStructure.build();
    }

    @Override
    protected void finish() {
        vote.conclude();
        voteStructure.remove();
    }

    public VillageVote votes() {
        return vote;
    }

    public static final class VillageVote extends PlayerVote {
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
