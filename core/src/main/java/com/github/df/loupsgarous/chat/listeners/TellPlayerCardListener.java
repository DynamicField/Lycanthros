package com.github.df.loupsgarous.chat.listeners;

import com.github.df.loupsgarous.chat.LGChatStuff;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.event.LGGameStartEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TellPlayerCardListener implements Listener {
    @EventHandler
    public void onGameStart(LGGameStartEvent event) {
        event.getOrchestrator().getPlayers().forEach(this::sendMessage);
    }

    private void sendMessage(LGPlayer player, Player minecraftPlayer) {
        String cardName = player.getCard().getName();
        String cardDescription = player.getCard().getDescription();
        boolean isFeminine = player.getCard().isFeminineName();

        String article = isFeminine ? "une" : "un";

        String message = LGChatStuff.BANNER + "\n" +
                         ChatColor.BLUE + "Tu est " + article + " " + ChatColor.GOLD + cardName + ChatColor.BLUE + ".\n" +
                         ChatColor.YELLOW + cardDescription + "\n" +
                         LGChatStuff.BANNER;

        minecraftPlayer.sendMessage("\n");
        minecraftPlayer.sendMessage(message);
    }

    private void sendMessage(LGPlayer lgPlayer) {
        lgPlayer.minecraft(mcPlayer -> sendMessage(lgPlayer, mcPlayer));
    }
}
