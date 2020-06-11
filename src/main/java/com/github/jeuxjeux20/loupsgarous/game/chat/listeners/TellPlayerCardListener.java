package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.LGChatStuff;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.events.LGGameStartEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TellPlayerCardListener implements Listener {
    @EventHandler
    public void onGameStart(LGGameStartEvent event) {
        event.getGame().getPlayers().forEach(this::sendMessage);
    }

    private void sendMessage(LGPlayer lgPlayer, Player minecraftPlayer) {
        String cardName = lgPlayer.getCard().getName();
        String cardDescription = lgPlayer.getCard().getDescription();

        String message = LGChatStuff.BANNER + "\n" +
                         ChatColor.BLUE + "Tu est un " + ChatColor.GOLD + cardName + ChatColor.BLUE + ".\n" +
                         ChatColor.YELLOW + cardDescription + "\n" +
                         LGChatStuff.BANNER;

        minecraftPlayer.sendMessage("\n");
        minecraftPlayer.sendMessage(message);
    }

    private void sendMessage(LGPlayer lgPlayer) {
        lgPlayer.getMinecraftPlayer().ifPresent(mcPlayer -> sendMessage(lgPlayer, mcPlayer));
    }
}
