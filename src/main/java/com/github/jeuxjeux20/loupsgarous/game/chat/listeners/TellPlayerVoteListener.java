package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableTypes;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Votable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteListener implements Listener {
    // TODO: Broader votables

    @EventHandler
    public void onPick(LGPickEvent<?, ?> event) {
        event.cast(LGInteractableTypes.PLAYER_VOTABLE).ifPresent(this::onVote);
    }

    private void onVote(LGPickEvent<LGPlayer, Votable<LGPlayer>> event) {
        Votable<LGPlayer> votable = event.getEntry().getValue();

        LGChatChannel channel = votable.getInfoMessagesChannel();
        String message = vote(player(event.getPicker().getName())) +
                         vote(" " + votable.getPointingText() + " ") +
                         vote(player(event.getTarget().getName()));

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
