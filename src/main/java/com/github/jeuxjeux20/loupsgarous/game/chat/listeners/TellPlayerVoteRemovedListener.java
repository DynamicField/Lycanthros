package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableTypes;
import com.github.jeuxjeux20.loupsgarous.game.interaction.Votable;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteRemovedListener implements Listener {
    // TODO: Broader votables

    @EventHandler
    public void onPickRemoved(LGPickRemovedEvent<?, ?> e) {
        e.cast(LGInteractableTypes.PLAYER_VOTABLE)
                .filter(LGPickRemovedEvent::isOrganic)
                .ifPresent(this::onVoteRemoved);
    }

    private void onVoteRemoved(LGPickRemovedEvent<LGPlayer, Votable<LGPlayer>> event) {
        LGChatChannel channel = event.getEntry().getValue().getInfoMessagesChannel();

        String message = vote(player(event.getPicker().getName())) +
                         vote(" retire son vote ") +
                         ChatColor.ITALIC + "(" + player(ChatColor.ITALIC + event.getTarget().getName()) +
                         vote(ChatColor.ITALIC + ")");

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
