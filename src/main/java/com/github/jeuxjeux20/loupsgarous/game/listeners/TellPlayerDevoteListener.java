package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.chat.LGGameChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGGameChatManager;
import com.github.jeuxjeux20.loupsgarous.game.events.LGPickRemovedEvent;
import com.google.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerDevoteListener implements Listener {
    private final LGGameChatManager chatManager;

    @Inject
    TellPlayerDevoteListener(LGGameChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @EventHandler
    public void onDevote(LGPickRemovedEvent event) {
        LGGameChatChannel channel = event.getPickableProvider().getInfoMessagesChannel();
        String message = vote(player(event.getFrom().getName())) +
                         vote(" retire son vote ") +
                         ChatColor.ITALIC + "(" + player(ChatColor.ITALIC + event.getTo().getName()) +
                         vote(ChatColor.ITALIC + ")");

        if (channel == null) {
            event.getOrchestrator().sendToEveryone(message);
        } else {
            chatManager.sendMessage(channel, message, event.getOrchestrator());
        }
    }
}
