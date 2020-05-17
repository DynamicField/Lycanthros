package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import me.lucko.helper.metadata.Metadata;
import me.lucko.helper.metadata.MetadataKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class DefaultLGScoreboardManager implements LGScoreboardManager {
    private static final MetadataKey<Scoreboard> SCOREBOARD_KEY
            = MetadataKey.create("lg_scoreboard", Scoreboard.class);

    @Override
    public void updatePlayer(LGPlayer player, LGGameOrchestrator orchestrator) {
        player.getMinecraftPlayer().ifPresent(minecraftPlayer -> {
            Scoreboard scoreboard = Metadata.provideForPlayer(minecraftPlayer).getOrPut(SCOREBOARD_KEY,
                    () -> Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());

            update(player, scoreboard, orchestrator);
            minecraftPlayer.setScoreboard(scoreboard);
        });
    }

    @Override
    public void removePlayer(Player player) {
        Metadata.provideForPlayer(player).get(SCOREBOARD_KEY).ifPresent(scoreboard -> {
            player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard());
            Metadata.provideForPlayer(player).remove(SCOREBOARD_KEY);
        });
    }

    private void update(LGPlayer player, Scoreboard scoreboard, LGGameOrchestrator orchestrator) {
        LGGame game = orchestrator.getGame();

        Objective objective = recreateObjective(scoreboard);

        if (orchestrator.isGameRunning()) {
            objective.getScore(ChatColor.AQUA + "Joueurs en vie : " + ChatColor.BOLD + game.getAlivePlayers().count())
                    .setScore(99);

            Optional<Votable> maybeVotable = orchestrator.getCurrentStage().getComponent(Votable.class,
                    x -> x.getCurrentState().canPlayerPick(player).isSuccess());

            maybeVotable.ifPresent(votable -> {
                Votable.VoteState voteState = votable.getCurrentState();
                if (voteState == null) return;

                LGPlayer playerWithMostVotes = voteState.getPlayerWithMostVotes();

                objective.getScore(ChatColor.LIGHT_PURPLE + "-= Votes =-").setScore(98);

                final Objective finalObjective = objective;
                voteState.getPlayersVoteCount().forEach((votedPlayer, voteCount) -> {
                    boolean isMostVotes = playerWithMostVotes == votedPlayer;
                    String prefix = isMostVotes ? ChatColor.RED.toString() + ChatColor.BOLD : "";

                    finalObjective.getScore(prefix + votedPlayer.getName()).setScore(voteCount);
                });
            });
        }
    }

    @NotNull
    private Objective recreateObjective(Scoreboard scoreboard) {
        Objective objective = scoreboard.getObjective("lg");
        if (objective != null) {
            objective.unregister();
        }
        objective = scoreboard.registerNewObjective("lg", "dummy",
                ChatColor.GREEN.toString() + ChatColor.BOLD + "Loups-Garous");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        return objective;
    }
}
