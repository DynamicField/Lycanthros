package com.github.jeuxjeux20.loupsgarous.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;

public class PlayersAliveScoreboardComponent implements ScoreboardComponent {
    @Override
    public ImmutableList<Line> render(LGPlayer player, LGGameOrchestrator orchestrator) {
        if (!orchestrator.isGameRunning()) return ImmutableList.of();

        return ImmutableList.of(
                new Line(ChatColor.AQUA + "Joueurs en vie : " +
                         ChatColor.BOLD + orchestrator.getAlivePlayers().count())
        );
    }
}
