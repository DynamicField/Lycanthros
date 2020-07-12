package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.AbstractPlayerVotable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTags;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@MajorityVoteShortensCountdown(LGInteractableKeys.Names.PLAYER_VOTE)
public class MaireElectionStage extends CountdownLGStage {
    private final MaireVotable votable;

    @Inject
    MaireElectionStage(LGGameOrchestrator orchestrator, MaireVotable votable) {
        super(orchestrator);

        this.votable = votable;

        registerInteractable(votable);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(60);
    }

    @Override
    protected void finish() {
        votable.conclude();
    }

    @Override
    public boolean isTemporary() {
        return true;
    }

    @Override
    public String getTitle() {
        return "Le village va élire un maire.";
    }

    @Override
    public String getName() {
        return "Élection du maire";
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.BLUE;
    }

    public MaireVotable votes() {
        return votable;
    }

    @OrchestratorScoped
    public static class MaireVotable extends AbstractPlayerVotable {
        private final Random random;

        @Inject
        MaireVotable(LGGameOrchestrator orchestrator, Random random) {
            super(orchestrator);
            this.random = random;
        }

        @Override
        protected PickConditions<LGPlayer> additionalVoteConditions() {
            return PickConditions.empty();
        }

        @Override
        public InteractableEntry<? extends Pickable<LGPlayer>> getEntry() {
            return new InteractableEntry<>(LGInteractableKeys.PLAYER_VOTE, this);
        }

        @Override
        public String getPointingText() {
            return "vote pour";
        }

        @Override
        protected boolean conclude(VoteOutcome<LGPlayer> outcome) {
            LGPlayer electedPlayer = outcome.getElected().orElseGet(this::drawRandomPlayer);

            orchestrator.tags().add(electedPlayer, LGTags.MAIRE);
            orchestrator.chat().sendToEveryone(
                    ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + electedPlayer.getName() +
                    ChatColor.DARK_AQUA + " a été élu maire."
            );

            return true;
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
