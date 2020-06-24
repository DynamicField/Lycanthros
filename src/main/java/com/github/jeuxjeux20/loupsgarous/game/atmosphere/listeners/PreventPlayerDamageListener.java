package com.github.jeuxjeux20.loupsgarous.game.atmosphere.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.google.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PreventPlayerDamageListener implements Listener {
    private final LGGameManager gameManager;

    @Inject
    PreventPlayerDamageListener(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player &&
            gameManager.getPlayerInGame((Player) event.getEntity()).isPresent()) {
            event.setCancelled(true);
        }
    }
}
