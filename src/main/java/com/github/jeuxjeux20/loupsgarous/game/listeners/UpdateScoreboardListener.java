package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.LGScoreboardManager;
import com.github.jeuxjeux20.loupsgarous.game.events.*;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

public class UpdateScoreboardListener implements Listener {
    private final LGScoreboardManager scoreboardManager;
    private final LGGameManager gameManager;

    @Inject
    public UpdateScoreboardListener(LGScoreboardManager scoreboardManager, LGGameManager gameManager) {
        this.scoreboardManager = scoreboardManager;
        this.gameManager = gameManager;
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
    public void onLGStageChanged(LGStageChangedEvent event) {
        updateAll(event);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Optional<LGPlayerAndGame> playerInGame = gameManager.getPlayerInGame(event.getPlayer());

        playerInGame.ifPresent(pg ->
                scoreboardManager.updatePlayer(pg.getPlayer(), pg.getOrchestrator()));
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
