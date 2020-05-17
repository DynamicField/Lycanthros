package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.events.LGTurnChangeEvent;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SwitchTimeOfDayListener implements Listener {
    @EventHandler
    public void onTimeChange(LGTurnChangeEvent event) {
        LGGameOrchestrator orchestrator = event.getOrchestrator();

        if (orchestrator.getTurn().getTime() == LGGameTurnTime.NIGHT) {
            orchestrator.getWorld().getCBWorld().setTime(13000);
            orchestrator.getAllMinecraftPlayers().forEach(player -> {
                player.sendTitle(ChatColor.RED + "C'est la nuit",
                        null,
                        10, 100, 10);
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, SoundCategory.MASTER, 0.5f, 1f);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10000, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10000, 2));
            });
        } else if (orchestrator.getTurn().getTime() == LGGameTurnTime.DAY) {
            orchestrator.getWorld().getCBWorld().setTime(1000);
            orchestrator.getAllMinecraftPlayers().forEach(player -> {
                player.sendTitle(ChatColor.GOLD + "C'est le jour",
                        null,
                        10, 100, 10);
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
            });
        } else {
            throw new NotImplementedException(orchestrator.getTurn().getTime() + " is unknown.");
        }
    }
}
