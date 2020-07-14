package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableTypes;
import com.github.jeuxjeux20.loupsgarous.game.interaction.PickData;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.Vote;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteListener implements Listener {
    // TODO: Broader votables

    @EventHandler
    public void onPick(LGPickEvent event) {
        event.getPickData().cast(LGInteractableTypes.PLAYER_VOTABLE).ifPresent(p -> onVote(event, p));
    }

    private void onVote(LGPickEvent event, PickData<LGPlayer, Vote<LGPlayer>> pickData) {
        Vote<LGPlayer> vote = pickData.getEntry().getValue();

        LGChatChannel channel = vote.getInfoMessagesChannel();
        String message = vote(player(pickData.getPicker().getName())) +
                         vote(" " + vote.getPointingText() + " ") +
                         vote(player(pickData.getTarget().getName()));

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
