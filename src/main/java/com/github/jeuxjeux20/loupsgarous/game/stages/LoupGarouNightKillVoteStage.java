package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGGameChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGGameChatManager;
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

public class LoupGarouNightKillVoteStage extends AsyncLGGameStage implements Votable, CountdownTimedStage {
    private final VoteState currentState;
    private final TickEventCountdown countdown;

    private final LGGameChatManager chatManager;
    private final LoupsGarousVoteChatChannel loupsGarousVoteChatChannel;

    @Inject
    public LoupGarouNightKillVoteStage(@Assisted LGGameOrchestrator orchestrator,
                                       LGGameChatManager chatManager,
                                       LoupsGarousVoteChatChannel loupsGarousVoteChatChannel) {
        super(orchestrator);

        this.chatManager = chatManager;
        this.loupsGarousVoteChatChannel = loupsGarousVoteChatChannel;

        currentState = createVoteState();
        countdown = new TickEventCountdown(this, 10);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.getTurn().getTime() == LGGameTurnTime.NIGHT;
    }

    @Override
    public CompletableFuture<Void> run() {
        return countdown.start().thenRun(this::computeVoteOutcome);
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
            chatManager.sendMessage(loupsGarousVoteChatChannel,
                    ChatColor.AQUA + "Les loups ont décidé de tuer " +
                    player(playerWithMostVotes.getName()) + ChatColor.AQUA + ".",
                    orchestrator);
        } else {
            chatManager.sendMessage(loupsGarousVoteChatChannel,
                    ChatColor.AQUA + "Les loups n'ont pas pu se décider !",
                    orchestrator);
        }
    }

    public VoteState getCurrentState() {
        return currentState;
    }

    @Override
    public TickEventCountdown getCountdown() {
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
    public LGGameChatChannel getInfoMessagesChannel() {
        return loupsGarousVoteChatChannel;
    }

    @Override
    public String getIndicator() {
        return "vote pour tuer";
    }
}
