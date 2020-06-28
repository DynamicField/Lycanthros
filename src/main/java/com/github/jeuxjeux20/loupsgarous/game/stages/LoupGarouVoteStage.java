package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.LGSoundStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LoupsGarousVoteChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.interaction.*;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKill;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.NightKillReason;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

import java.util.Set;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;

@MajorityVoteShortensCountdown(timeLeft = 10)
public class LoupGarouVoteStage extends CountdownLGStage implements InteractableProvider {
    private final LoupGarouVotable votable;
    private final LoupsGarousVoteChatChannel voteChannel;

    private boolean isVoteSuccessful;

    @Inject
    LoupGarouVoteStage(@Assisted LGGameOrchestrator orchestrator,
                       LoupsGarousVoteChatChannel voteChannel) {
        super(orchestrator);

        this.voteChannel = voteChannel;
        this.votable = new LoupGarouVotable();

        bind(votable);
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
        votable.close();
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

    @Override
    public Set<InteractableEntry<?>> getInteractables() {
        return ImmutableSet.of(votable.getEntry());
    }

    public final class LoupGarouVotable extends AbstractPlayerVotable {
        private LoupGarouVotable() {
            super(LoupGarouVoteStage.this.orchestrator, LGInteractableKeys.PLAYER_VOTE);
        }

        @Override
        public PickConditions<LGPlayer> conditions() {
            return FunctionalPickConditions.builder(super.conditions())
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
