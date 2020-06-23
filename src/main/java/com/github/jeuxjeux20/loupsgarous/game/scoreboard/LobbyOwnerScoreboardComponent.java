package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyOwnerChangeEvent;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;

public class LobbyOwnerScoreboardComponent implements ScoreboardComponent {
    @Override
    public ImmutableList<Line> render(LGPlayer player, LGGameOrchestrator orchestrator) {
        if (orchestrator.lobby().isLocked()) return ImmutableList.of();

        LGPlayer owner = orchestrator.lobby().getOwner();

        return ImmutableList.of(new Line("Partie de " + ChatColor.GREEN + owner.getName()));
    }

    @Override
    public ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of(LGLobbyOwnerChangeEvent.class);
    }
}
