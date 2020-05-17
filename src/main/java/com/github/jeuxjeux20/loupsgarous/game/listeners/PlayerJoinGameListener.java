package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerJoinGameListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerChangedWorldEvent event) {
        if (!event.getPlayer().getWorld().getName().startsWith(LGGameManager.WORLD_PREFIX)) return;
        event.getPlayer().sendTitle(ChatColor.YELLOW + "Loups-Garous",
                "Bienvenue !",
                10, 100, 10);


    }
}
