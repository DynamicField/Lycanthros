package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.events.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteListener implements Listener {
    @EventHandler
    public void onVote(LGPickEvent event) {
        if (!(event.getPickableProvider() instanceof Votable)) {
            return;
        }
        Votable votable = ((Votable) event.getPickableProvider());

        LGChatChannel channel = votable.getInfoMessagesChannel();
        String message = vote(player(event.getFrom().getName())) +
                         vote(" " + votable.getIndicator() + " ") +
                         vote(player(event.getTo().getName()));

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
