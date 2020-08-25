package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.chat.LoupsGarousVoteChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.interaction.InteractableRegisterer;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.AbstractPlayerVote;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.outcome.VoteOutcome;
import com.github.jeuxjeux20.loupsgarous.kill.causes.NightKillCause;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.inject.Inject;
import org.bukkit.ChatColor;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

@MajorityVoteShortensCountdown(value = LGInteractableKeys.Names.PLAYER_VOTE, timeLeft = 10)
@PhaseInfo(
        name = "Loups-garous",
        title = "Les loups vont dévorer un innocent...",
        color = PhaseColor.RED
)
public final class LoupGarouVotePhase extends CountdownLGPhase {
    private final LoupGarouVote votable;
    private final LoupsGarousVoteChatChannel voteChannel;

    @Inject
    LoupGarouVotePhase(LGGameOrchestrator orchestrator,
                       LoupsGarousVoteChatChannel voteChannel,
                       InteractableRegisterer<LoupGarouVote> votable) {
        super(orchestrator);

        this.voteChannel = voteChannel;
        this.votable = votable.as(LGInteractableKeys.PLAYER_VOTE).boundWith(this);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.getTurn().getTime() == LGGameTurnTime.NIGHT &&
               votable.canSomeonePick();
    }

    @Override
    protected void finish() {
        if (votable.conclude()) {
            howl();
        }
    }

    private void howl() {
        orchestrator.getPlayers().stream()
                .filter(voteChannel::isReadable)
                .map(LGPlayer::minecraft)
                .flatMap(OptionalUtils::stream)
                .forEach(LGSoundStuff::howl);
    }

    public LoupGarouVote votes() {
        return votable;
    }

    @OrchestratorScoped
    public static final class LoupGarouVote extends AbstractPlayerVote {
        private final LoupsGarousVoteChatChannel voteChannel;

        @Inject
        LoupGarouVote(LGGameOrchestrator orchestrator,
                      Dependencies dependencies,
                      LoupsGarousVoteChatChannel voteChannel) {
            super(orchestrator, dependencies);
            this.voteChannel = voteChannel;
        }

        @Override
        protected boolean conclude(VoteOutcome<LGPlayer> outcome) {
            Optional<LGPlayer> maybeMajority = outcome.getElected();

            if (maybeMajority.isPresent()) {
                LGPlayer majority = maybeMajority.get();

                majority.dieLater(NightKillCause.INSTANCE);
                orchestrator.chat().sendMessage(voteChannel,
                        ChatColor.AQUA + "Les loups ont décidé de tuer " +
                        player(majority.getName()) + ChatColor.AQUA + "."
                );
            } else {
                orchestrator.chat().sendMessage(voteChannel,
                        ChatColor.AQUA + "Les loups n'ont pas pu se décider !"
                );
            }

            return maybeMajority.isPresent();
        }

        @Override
        public PickConditions<LGPlayer> additionalVoteConditions() {
            return conditionsBuilder()
                    .ensurePicker(this::isLoupGarou, "Vous n'êtes pas loup-garou !")
                    .build();
        }

        @Override
        public String getPointingText() {
            return "vote pour tuer";
        }

        @Override
        public ChatColor getHighlightColor() {
            return ChatColor.RED;
        }

        @Override
        public LGChatChannel getInfoMessagesChannel() {
            return voteChannel;
        }

        private boolean isLoupGarou(LGPlayer picker) {
            return picker.teams().has(LGTeams.LOUPS_GAROUS);
        }
    }
}
