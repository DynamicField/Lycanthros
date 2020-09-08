package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.Interactable;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.AbstractPlayerVote;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.tags.LGTags;
import com.github.jeuxjeux20.relativesorting.Order;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

@MajorityVoteShortensCountdown(LGInteractableKeys.Names.PLAYER_VOTE)
@PhaseInfo(
        name = "Élection du maire",
        title = "Le village va élire un maire.",
        color = PhaseColor.BLUE,
        isTemporary = true
)
@Order(after = RevealAllKillsPhase.class, before = VillageVotePhase.class)
public final class MaireElectionPhase extends CountdownLGPhase {
    private final MaireVote vote;

    public MaireElectionPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        this.vote = Interactable.createBound(MaireVote::new, LGInteractableKeys.PLAYER_VOTE, this);
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

    public static final class MaireVote extends AbstractPlayerVote {
        public MaireVote(LGGameOrchestrator orchestrator) {
            super(orchestrator);
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
            return players.get(RandomUtils.nextInt(players.size()));
        }

        @Override
        public ChatColor getHighlightColor() {
            return ChatColor.AQUA;
        }
    }
}
