package com.github.jeuxjeux20.loupsgarous.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyOwnerChangeEvent;
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
        return ImmutableList.of(LGLobbyOwnerChangeEvent.class);
    }
}
