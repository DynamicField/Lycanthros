package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerJoinEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinGameListener implements Listener {
    @EventHandler
    public void onPlayerJoin(LGPlayerJoinEvent event) {
        event.getPlayer().sendTitle(ChatColor.YELLOW + "Loups-Garous",
                "Bienvenue !",
                10, 100, 10);
    }
}
