package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.stage.LGStageChangingEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;

import java.util.Optional;

public class CurrentVotesScoreboardComponent implements ScoreboardComponent {
    @Override
    public ImmutableList<Line> render(LGPlayer player, LGGameOrchestrator orchestrator) {
        if (!orchestrator.isGameRunning()) return ImmutableList.of();

        ImmutableList.Builder<Line> lines = ImmutableList.builder();

        Optional<Votable> maybeVotable = orchestrator.stages().current().getComponent(Votable.class,
                x -> x.getCurrentState().canPlayerPick(player).isSuccess());

        maybeVotable.ifPresent(votable -> {
            lines.add(new Line(ChatColor.LIGHT_PURPLE + "-= Votes =-"));

            Votable.VoteState voteState = votable.getCurrentState();
            LGPlayer playerWithMostVotes = voteState.getPlayerWithMostVotes();

            voteState.getPlayersVoteCount().forEach((votedPlayer, voteCount) -> {
                boolean isMostVotes = playerWithMostVotes == votedPlayer;
                String color = isMostVotes ? ChatColor.RED.toString() + ChatColor.BOLD : "";

                lines.add(new Line(color + votedPlayer.getName(), voteCount));
            });
        });

        return lines.build();
    }

    @Override
    public ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of(LGPickEvent.class, LGPickRemovedEvent.class, LGStageChangingEvent.class);
    }
}
