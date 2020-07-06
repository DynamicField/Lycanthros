package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LoupsGarousVoteChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.AbstractPlayerVotable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.Votable;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.NightKillReason;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

@MajorityVoteShortensCountdown(value = LGInteractableKeys.Names.PLAYER_VOTE, timeLeft = 10)
public class LoupGarouVoteStage extends CountdownLGStage {
    private final LoupGarouVotable votable;
    private final LoupsGarousVoteChatChannel voteChannel;

    private boolean isVoteSuccessful;

    @Inject
    LoupGarouVoteStage(LGGameOrchestrator orchestrator,
                       LoupsGarousVoteChatChannel voteChannel,
                       AbstractPlayerVotable.PlayerVoteDependencies voteDependencies) {
        super(orchestrator);

        this.voteChannel = voteChannel;
        this.votable = new LoupGarouVotable(voteDependencies);

        registerInteractable(votable);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.turn().getTime() == LGGameTurnTime.NIGHT &&
               votable.canSomeonePick();
    }

    @Override
    protected void finish() {
        votable.closeAndReportException();
        computeVoteOutcome();
        howl();
    }

    @Override
    public String getName() {
        return "Loups-Garous";
    }

    @Override
    public String getTitle() {
        return "Les loups vont dévorer un innocent...";
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.RED;
    }

    private void computeVoteOutcome() {
        Optional<LGPlayer> maybeMajority = votable.getOutcome().getElected();
        isVoteSuccessful = maybeMajority.isPresent();

        if (isVoteSuccessful) {
            LGPlayer majority = maybeMajority.get();

            orchestrator.kills().pending().add(LGKill.of(majority, NightKillReason.INSTANCE));
            orchestrator.chat().sendMessage(voteChannel,
                    ChatColor.AQUA + "Les loups ont décidé de tuer " +
                    player(majority.getName()) + ChatColor.AQUA + "."
            );
        } else {
            orchestrator.chat().sendMessage(voteChannel,
                    ChatColor.AQUA + "Les loups n'ont pas pu se décider !"
            );
        }
    }

    private void howl() {
        if (!isVoteSuccessful) return;

        orchestrator.game().getPlayers().stream()
                .filter(p -> voteChannel.isReadable(p, orchestrator))
                .map(LGPlayer::getMinecraftPlayer)
                .flatMap(OptionalUtils::stream)
                .forEach(LGSoundStuff::howl);
    }

    public LoupGarouVotable votes() {
        return votable;
    }

    public final class LoupGarouVotable extends AbstractPlayerVotable {
        private LoupGarouVotable(PlayerVoteDependencies dependencies) {
            super(LoupGarouVoteStage.this.orchestrator, dependencies);
        }

        @Override
        public InteractableEntry<Votable<LGPlayer>> getEntry() {
            return new InteractableEntry<>(LGInteractableKeys.PLAYER_VOTE, this);
        }

        @Override
        public PickConditions<LGPlayer> additionalVoteConditions() {
            return FunctionalPickConditions.<LGPlayer>builder()
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
            return picker.getCard().isInTeam(LGTeams.LOUPS_GAROUS);
        }
    }
}
