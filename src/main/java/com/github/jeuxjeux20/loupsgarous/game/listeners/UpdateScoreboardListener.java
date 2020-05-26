package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGScoreboardManager;
import com.github.jeuxjeux20.loupsgarous.game.events.*;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.stage.LGStageChangeEvent;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UpdateScoreboardListener implements Listener {
    private final LGScoreboardManager scoreboardManager;

    @Inject
    public UpdateScoreboardListener(LGScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }

    @EventHandler
    public void onLGGameStart(LGGameStartEvent event) {
        updateAll(event);
    }

    @EventHandler
    public void onLGVote(LGPickEvent event) {
        updateAll(event);
    }

    @EventHandler
    public void onLGDevote(LGPickRemovedEvent event) {
        updateAll(event);
    }

    @EventHandler
    public void onLGStageChanged(LGStageChangeEvent event) {
        updateAll(event);
    }

    @EventHandler
    public void onPlayerJoin(LGPlayerJoinEvent event) {
        scoreboardManager.updatePlayer(event.getLGPlayer(), event.getOrchestrator());
    }

    @EventHandler
    public void onLGGameFinished(LGGameFinishedEvent event) {
        for (LGPlayer player : event.getGame().getPlayers()) {
            scoreboardManager.removePlayer(player);
        }
    }

    private void updateAll(LGEvent event) {
        for (LGPlayer player : event.getGame().getPlayers()) {
            scoreboardManager.updatePlayer(player, event.getOrchestrator());
        }
    }
}
