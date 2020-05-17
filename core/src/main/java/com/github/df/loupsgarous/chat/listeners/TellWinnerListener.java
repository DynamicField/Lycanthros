package com.github.df.loupsgarous.chat.listeners;

import com.github.df.loupsgarous.chat.LGChatStuff;
import com.github.df.loupsgarous.event.LGGameFinishedEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TellWinnerListener implements Listener {
    @EventHandler
    public void onGameFinish(LGGameFinishedEvent event) {
        String endingMessage = event.getEnding().getMessage();

        String message = LGChatStuff.BANNER + "\n" +
                         ChatColor.GOLD + "La partie est terminée !\n" +
                         ChatColor.GOLD + ChatColor.BOLD + endingMessage + "\n" +
                         LGChatStuff.BANNER;

        event.getOrchestrator().chat().sendToEveryone(message);
    }
}
