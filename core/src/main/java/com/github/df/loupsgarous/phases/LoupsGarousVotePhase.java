package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.Countdown;
import com.github.df.loupsgarous.LGSoundStuff;
import com.github.df.loupsgarous.chat.ChatChannel;
import com.github.df.loupsgarous.chat.LGChatChannels;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGGameTurnTime;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.LGInteractableKeys;
import com.github.df.loupsgarous.interaction.condition.PickConditions;
import com.github.df.loupsgarous.interaction.vote.PlayerVote;
import com.github.df.loupsgarous.interaction.vote.outcome.VoteOutcome;
import com.github.df.loupsgarous.kill.causes.NightKillCause;
import com.github.df.loupsgarous.teams.LGTeams;
import com.github.df.loupsgarous.util.OptionalUtils;
import org.bukkit.ChatColor;

import java.util.Optional;

import static com.github.df.loupsgarous.chat.LGChatStuff.player;

@MajorityVoteShortensCountdown(value = LGInteractableKeys.PLAYER_VOTE, timeLeft = 10)
@PhaseInfo(
        name = "Loups-garous",
        title = "Les loups vont dévorer un innocent...",
        color = PhaseColor.RED
)
public final class LoupsGarousVotePhase extends CountdownPhase {
    private final LoupGarouVote votable;

    public LoupsGarousVotePhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        this.votable = new LoupGarouVote(orchestrator);
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
    protected void start() {
        votable.register(LGInteractableKeys.PLAYER_VOTE).bindWith(this);
    }

    @Override
    protected void finish() {
        if (votable.conclude()) {
            howl();
        }  
    }

    private void howl() {
        orchestrator.getPlayers().stream()
                .filter(player -> LGChatChannels.LOUPS_GAROUS_VOTE.getView(player).isReadable())
                .map(LGPlayer::minecraft)
                .flatMap(OptionalUtils::stream)
                .forEach(LGSoundStuff::howl);
    }

    public LoupGarouVote votes() {
        return votable;
    }

    public static final class LoupGarouVote extends PlayerVote {
        public LoupGarouVote(LGGameOrchestrator orchestrator) {
            super(orchestrator);
        }

        @Override
        protected boolean conclude(VoteOutcome<LGPlayer> outcome) {
            Optional<LGPlayer> maybeMajority = outcome.getElected();

            if (maybeMajority.isPresent()) {
                LGPlayer majority = maybeMajority.get();

                majority.dieLater(NightKillCause.INSTANCE);
                orchestrator.chat().sendMessage(LGChatChannels.LOUPS_GAROUS_VOTE,
                        ChatColor.AQUA + "Les loups ont décidé de tuer " +
                        player(majority.getName()) + ChatColor.AQUA + "."
                );
            } else {
                orchestrator.chat().sendMessage(LGChatChannels.LOUPS_GAROUS_VOTE,
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
        public ChatChannel getInfoMessagesChannel() {
            return LGChatChannels.LOUPS_GAROUS_VOTE;
        }

        private boolean isLoupGarou(LGPlayer picker) {
            return picker.teams().has(LGTeams.LOUPS_GAROUS);
        }
    }
}
