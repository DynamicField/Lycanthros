package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LoupsGarousVoteChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.NightKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

@MajorityVoteShortensCountdown(timeLeft = 10)
public class LoupGarouVoteStage extends RunnableLGStage implements Votable, UnmodifiedCountdownTimedStage {
    private final VoteState currentState;
    private final Countdown unmodifiedCountdown;
    private final Countdown countdown;

    private final LoupsGarousVoteChatChannel voteChannel;

    private boolean isVoteSuccessful;

    @Inject
    LoupGarouVoteStage(@Assisted LGGameOrchestrator orchestrator,
                       LoupsGarousVoteChatChannel voteChannel) {
        super(orchestrator);

        this.voteChannel = voteChannel;

        currentState = createVoteState();

        unmodifiedCountdown = Countdown.builder(30).build(orchestrator);
        countdown = Countdown.builder()
                .apply(this::addTickEvents)
                .apply(Countdown.syncWith(unmodifiedCountdown))
                .finished(this::computeVoteOutcome)
                .finished(this::howl)
                .build(orchestrator);

        bind(currentState);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.turn().getTime() == LGGameTurnTime.NIGHT;
    }

    @Override
    public CompletableFuture<Void> execute() {
        return countdown.start();
    }

    @NotNull
    private VoteState createVoteState() {
        return new VoteState(orchestrator, this) {
            @Override
            public Check canPlayerPick(@NotNull LGPlayer player) {
                return super.canPlayerPick(player)
                        .and(player.getCard().isInTeam(LGTeams.LOUPS_GAROUS),
                                "Impossible de voter, car vous n'êtes pas loup-garou !");
            }
        };
    }

    private void computeVoteOutcome() {
        LGPlayer playerWithMostVotes = currentState.getPlayerWithMostVotes();
        if (playerWithMostVotes != null) {
            orchestrator.kills().pending().add(LGKill.of(playerWithMostVotes, NightKillReason::new));
            orchestrator.chat().sendMessage(voteChannel,
                    ChatColor.AQUA + "Les loups ont décidé de tuer " +
                    player(playerWithMostVotes.getName()) + ChatColor.AQUA + "."
            );
            isVoteSuccessful = true;
        } else {
            orchestrator.chat().sendMessage(voteChannel,
                    ChatColor.AQUA + "Les loups n'ont pas pu se décider !"
            );
            isVoteSuccessful = false;
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

    public VoteState getCurrentState() {
        return currentState;
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public Countdown getUnmodifiedCountdown() {
        return unmodifiedCountdown;
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

    @Override
    public LGChatChannel getInfoMessagesChannel() {
        return voteChannel;
    }

    @Override
    public String getIndicator() {
        return "vote pour tuer";
    }
}
