package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteListener implements Listener {
    @EventHandler
    public void onPick(LGPickEvent<?, ?> event) {
        event.cast(Votable.class).ifPresent(this::onVote);
    }

    private void onVote(LGPickEvent<LGPlayer, Votable> event) {
        Votable votable = event.getPickableProvider();

        LGChatChannel channel = votable.getInfoMessagesChannel();
        String message = vote(player(event.getPicker().getName())) +
                         vote(" " + votable.getIndicator() + " ") +
                         vote(player(event.getTarget().getName()));

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
