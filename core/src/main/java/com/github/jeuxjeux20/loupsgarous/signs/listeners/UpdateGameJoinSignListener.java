package com.github.jeuxjeux20.loupsgarous.signs.listeners;

import com.github.jeuxjeux20.loupsgarous.signs.GameJoinSignManager;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.Nullable;

public class UpdateGameJoinSignListener implements Listener {
    private final GameJoinSignManager signManager;

    @Inject
    UpdateGameJoinSignListener(GameJoinSignManager signManager) {
        this.signManager = signManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        Sign sign = (Sign) event.getBlock().getState();
        String gameName = getSignGameName(event);

        if (gameName == null) {
            event.getPlayer().sendMessage(ChatColor.AQUA + "Panneau supprimé !");
            signManager.deleteSignData(sign);
        } else {
            event.setLine(0, ChatColor.AQUA.toString() + ChatColor.BOLD + "[LoupsGarous]");
            signManager.updateSignData(sign, gameName);
            event.getPlayer().sendMessage(ChatColor.GREEN + "Panneau mis à jour : " + gameName);
        }
    }


    private @Nullable String getSignGameName(SignChangeEvent event) {
        String firstLine = event.getLine(0);
        String gameNameLine = event.getLine(1);

        if (!"[LoupsGarous]".equalsIgnoreCase(ChatColor.stripColor(firstLine)) ||
            Strings.isNullOrEmpty(gameNameLine)) {
            return null;
        } else {
            return gameNameLine;
        }
    }
}
