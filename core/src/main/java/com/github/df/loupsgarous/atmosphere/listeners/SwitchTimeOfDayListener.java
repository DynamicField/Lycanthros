package com.github.df.loupsgarous.atmosphere.listeners;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.event.LGGameFinishedEvent;
import com.github.df.loupsgarous.event.LGTurnChangeEvent;
import com.github.df.loupsgarous.event.player.LGPlayerQuitEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class SwitchTimeOfDayListener implements Listener {
    @EventHandler
    public void onTimeChange(LGTurnChangeEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        switch (orchestrator.getTurn().getTime()) {
            case NIGHT:
                orchestrator.getWorld().setTime(13000);
                orchestrator.getAllMinecraftPlayers().forEach(player -> {
                    player.sendTitle(ChatColor.RED + "C'est la nuit",
                            null,
                            10, 100, 10);
                    player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, SoundCategory.MASTER, 0.5f, 1f);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10000, 1));
                });
                break;
            case DAY:
                orchestrator.getWorld().setTime(1000);
                orchestrator.getAllMinecraftPlayers().forEach(player -> {
                    player.sendTitle(ChatColor.GOLD + "C'est le jour",
                            null,
                            10, 100, 10);
                    removeAllEffects(player);
                });
                break;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLGPlayerQuit(LGPlayerQuitEvent event) {
        event.getLGPlayer().minecraftNoContext(this::removeAllEffects);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLGGameFinished(LGGameFinishedEvent event) {
        event.getOrchestrator().getAllMinecraftPlayers().forEach(this::removeAllEffects);
    }

    private void removeAllEffects(@NotNull Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
}
