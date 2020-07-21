package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.AbstractPlayerVote;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.outcome.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTags;
import com.google.inject.Inject;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@MajorityVoteShortensCountdown(LGInteractableKeys.Names.PLAYER_VOTE)
@StageInfo(
        name = "Élection du maire",
        title = "Le village va élire un maire.",
        color = StageColor.BLUE,
        isTemporary = true
)
public final class MaireElectionStage extends CountdownLGStage {
    private final MaireVote vote;

    @Inject
    MaireElectionStage(LGGameOrchestrator orchestrator,
                       InteractableRegisterer<MaireVote> vote) {
        super(orchestrator);

        this.vote = vote.as(LGInteractableKeys.PLAYER_VOTE).boundWith(this);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(60);
    }

    @Override
    protected void finish() {
        vote.conclude();
    }

    public MaireVote votes() {
        return vote;
    }

    @OrchestratorScoped
    public static final class MaireVote extends AbstractPlayerVote {
        private final Random random;

        @Inject
        MaireVote(LGGameOrchestrator orchestrator, Dependencies dependencies, Random random) {
            super(orchestrator, dependencies);
            this.random = random;
        }

        @Override
        protected PickConditions<LGPlayer> additionalVoteConditions() {
            return PickConditions.empty();
        }

        @Override
        protected boolean conclude(VoteOutcome<LGPlayer> outcome) {
            LGPlayer electedPlayer = outcome.getElected().orElseGet(this::drawRandomPlayer);

            electedPlayer.tags().add(LGTags.MAIRE);
            orchestrator.chat().sendToEveryone(
                    ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + electedPlayer.getName() +
                    ChatColor.DARK_AQUA + " a été élu maire."
            );

            return true;
        }

        @Override
        public String getPointingText() {
            return "vote pour";
        }

        private LGPlayer drawRandomPlayer() {
            List<LGPlayer> players = getEligibleTargets().collect(Collectors.toList());
            return players.get(random.nextInt(players.size()));
        }

        @Override
        public ChatColor getHighlightColor() {
            return ChatColor.AQUA;
        }
    }
}
