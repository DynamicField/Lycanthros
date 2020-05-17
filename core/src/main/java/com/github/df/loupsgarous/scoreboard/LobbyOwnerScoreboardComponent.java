package com.github.df.loupsgarous.scoreboard;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.event.LGEvent;
import com.github.df.loupsgarous.event.lobby.LGOwnerChangeEvent;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;

public class LobbyOwnerScoreboardComponent implements ScoreboardComponent {
    @Override
    public ImmutableList<Line> render(LGPlayer player, LGGameOrchestrator orchestrator) {
        if (!orchestrator.allowsJoin()) return ImmutableList.of();

        LGPlayer owner = orchestrator.getOwner();

        if (owner != null) {
            return ImmutableList.of(new Line("Partie de " + ChatColor.GREEN + owner.getName()));
        } else {
            return ImmutableList.of();
        }
    }

    @Override
    public ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of(LGOwnerChangeEvent.class);
    }
}
