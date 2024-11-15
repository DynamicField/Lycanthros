package com.github.df.loupsgarous.atmosphere.listeners;

import com.github.df.loupsgarous.game.LGGameManager;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGGameTurnTime;
import com.google.inject.Inject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PreventNightPlayerMovementListener implements Listener {
    private final LGGameManager gameManager;

    @Inject
    PreventNightPlayerMovementListener(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        gameManager.getPlayerInGame(player).ifPresent(pg -> {
            LGGameOrchestrator orchestrator = pg.getOrchestrator();

            if (orchestrator.isGameRunning() &&
                orchestrator.getTurn().getTime() == LGGameTurnTime.NIGHT) {
                Location to = event.getTo();
                Location from = event.getFrom();

                if (to != null) {
                    to.setX(from.getX());
                    to.setY(Math.min(from.getY(), to.getY())); // Still apply gravity.
                    to.setZ(from.getZ());
                    // Do not change the pitch and yaw.
                }
            }
        });
    }

}
