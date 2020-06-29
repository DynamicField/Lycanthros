package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LoupsGarousVoteChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.interaction.AbstractPlayerVotable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.InteractableEntry;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableKeys;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Votable;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.NightKillReason;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

@MajorityVoteShortensCountdown(value = LGInteractableKeys.Names.PLAYER_VOTE, timeLeft = 10)
public class LoupGarouVoteStage extends CountdownLGStage {
    private final LoupGarouVotable votable;
    private final LoupsGarousVoteChatChannel voteChannel;

    private boolean isVoteSuccessful;

    @Inject
    LoupGarouVoteStage(@Assisted LGGameOrchestrator orchestrator,
                       LoupsGarousVoteChatChannel voteChannel) {
        super(orchestrator);

        this.voteChannel = voteChannel;
        this.votable = new LoupGarouVotable();

        orchestrator.interactables().put(bind(votable));
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.turn().getTime() == LGGameTurnTime.NIGHT;
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
        LGPlayer playerWithMostVotes = votable.getMajorityTarget();
        isVoteSuccessful = playerWithMostVotes != null;

        if (isVoteSuccessful) {
            orchestrator.kills().pending().add(LGKill.of(playerWithMostVotes, NightKillReason::new));
            orchestrator.chat().sendMessage(voteChannel,
                    ChatColor.AQUA + "Les loups ont décidé de tuer " +
                    player(playerWithMostVotes.getName()) + ChatColor.AQUA + "."
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
                .filter(p -> voteChannel.areMessagesVisibleTo(p, orchestrator))
                .map(LGPlayer::getMinecraftPlayer)
                .flatMap(OptionalUtils::stream)
                .forEach(LGSoundStuff::howl);
    }

    public LoupGarouVotable votes() {
        return votable;
    }

    public final class LoupGarouVotable extends AbstractPlayerVotable<Votable<LGPlayer>> {
        private LoupGarouVotable() {
            super(LoupGarouVoteStage.this.orchestrator);
        }

        @Override
        public InteractableEntry<Votable<LGPlayer>> getEntry() {
            return new InteractableEntry<>(LGInteractableKeys.PLAYER_VOTE, this);
        }

        @Override
        public PickConditions<LGPlayer> additionalConditions() {
            return FunctionalPickConditions.<LGPlayer>builder()
                    .ensurePicker(this::isLoupGarou, "Impossible de voter, car vous n'êtes pas loup-garou !")
                    .build();
        }

        @Override
        public String getPointingText() {
            return "vote pour tuer";
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
