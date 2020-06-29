package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableTypes;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Pick;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Votable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteListener implements Listener {
    // TODO: Broader votables

    @EventHandler
    public void onPick(LGPickEvent event) {
        event.getPick().cast(LGInteractableTypes.PLAYER_VOTABLE).ifPresent(p -> onVote(event, p));
    }

    private void onVote(LGPickEvent event, Pick<LGPlayer, Votable<LGPlayer>> pick) {
        Votable<LGPlayer> votable = pick.getEntry().getValue();

        LGChatChannel channel = votable.getInfoMessagesChannel();
        String message = vote(player(pick.getPicker().getName())) +
                         vote(" " + votable.getPointingText() + " ") +
                         vote(player(pick.getTarget().getName()));

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
