package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LoupsGarousVoteChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.killreasons.NightKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

@MajorityVoteShortensCountdown(timeLeft = 10)
public class LoupGarouNightKillVoteStage extends AsyncLGGameStage implements Votable, DualCountdownStage {
    private final VoteState currentState;
    private final Countdown unmodifiedCountdown;
    private final TickEventCountdown countdown;

    private final LoupsGarousVoteChatChannel voteChannel;

    @Inject
    LoupGarouNightKillVoteStage(@Assisted LGGameOrchestrator orchestrator,
                                LoupsGarousVoteChatChannel voteChannel) {
        super(orchestrator);

        this.voteChannel = voteChannel;

        currentState = createVoteState();
        unmodifiedCountdown = new Countdown(orchestrator.getPlugin(), 30);
        countdown = new TickEventCountdown(this, unmodifiedCountdown.getTimer());
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.getTurn().getTime() == LGGameTurnTime.NIGHT;
    }

    @Override
    public CompletableFuture<Void> run() {
        unmodifiedCountdown.start();
        return cancelRoot(countdown.start(), f -> f.thenRun(this::computeVoteOutcome));
    }

    @NotNull
    private VoteState createVoteState() {
        return new VoteState(orchestrator, this) {
            @Override
            public Check canPlayerPick(@NotNull LGPlayer player) {
                return super.canPlayerPick(player)
                        .and(player.getCard().getTeams().contains(LGTeams.LOUPS_GAROUS),
                                "Impossible de voter, car vous n'êtes pas loup-garou !");
            }
        };
    }

    private void computeVoteOutcome() {
        LGPlayer playerWithMostVotes = currentState.getPlayerWithMostVotes();
        if (playerWithMostVotes != null) {
            orchestrator.getPendingKills().add(LGKill.of(playerWithMostVotes, NightKillReason::new));
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

    public VoteState getCurrentState() {
        return currentState;
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public @NotNull String getName() {
        return "Loups-Garous";
    }

    @Override
    public Optional<String> getTitle() {
        return Optional.of("Les loups vont dévorer un innocent...");
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

    @Override
    public Countdown getUnmodifiedCountdown() {
        return unmodifiedCountdown;
    }
}
