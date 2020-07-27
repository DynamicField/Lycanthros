package com.github.jeuxjeux20.loupsgarous.listeners;

import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerJoinEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GreetPlayerOnJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(LGPlayerJoinEvent event) {
        event.getPlayer().sendTitle(ChatColor.YELLOW + "Loups-Garous",
                "Bienvenue !",
                10, 100, 10);
    }
}
