package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.events.LGGameFinishedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;

public class ClearAllEffectsOnEndListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onLGGameFinished(LGGameFinishedEvent event) {
        event.getOrchestrator().getAllMinecraftPlayers().forEach(player -> {
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        });
    }
}
