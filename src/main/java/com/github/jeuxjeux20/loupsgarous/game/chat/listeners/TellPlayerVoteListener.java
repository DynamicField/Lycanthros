package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.PlayerVotable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteListener implements Listener {
    // TODO: Broader votables

    @EventHandler
    public void onPick(LGPickEvent<?, ?> event) {
        event.cast(PlayerVotable.class).ifPresent(this::onVote);
    }

    private void onVote(LGPickEvent<LGPlayer, PlayerVotable> event) {
        PlayerVotable votable = event.getPickable();

        LGChatChannel channel = votable.getInfoMessagesChannel();
        String message = vote(player(event.getPicker().getName())) +
                         vote(" " + votable.getIndicator() + " ") +
                         vote(player(event.getTarget().getName()));

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
