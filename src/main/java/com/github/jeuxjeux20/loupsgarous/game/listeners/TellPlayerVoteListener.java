package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.chat.LGGameChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGGameChatManager;
import com.github.jeuxjeux20.loupsgarous.game.events.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import com.google.inject.Inject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteListener implements Listener {
    private final LGGameChatManager chatManager;

    @Inject
    public TellPlayerVoteListener(LGGameChatManager chatManager) {
        this.chatManager = chatManager;
    }

    @EventHandler
    public void onVote(LGPickEvent event) {
        if (!(event.getPickableProvider() instanceof Votable)) {
            return;
        }
        Votable votable = ((Votable) event.getPickableProvider());

        LGGameChatChannel channel = votable.getInfoMessagesChannel();
        String message = vote(player(event.getFrom().getName())) +
                         vote(" " + votable.getIndicator() + " ") +
                         vote(player(event.getTo().getName()));

        if (channel == null) {
            event.getOrchestrator().sendToEveryone(message);
        } else {
            chatManager.sendMessage(channel, message, event.getOrchestrator());
        }
    }
}
