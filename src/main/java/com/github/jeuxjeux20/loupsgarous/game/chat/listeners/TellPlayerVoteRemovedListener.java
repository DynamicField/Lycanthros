package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.LGInteractableTypes;
import com.github.jeuxjeux20.loupsgarous.game.interaction.PickData;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.Vote;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteRemovedListener implements Listener {
    // TODO: Broader votables

    @EventHandler
    public void onPickRemoved(LGPickRemovedEvent event) {
        if (!event.isOrganic()) {
            return;
        }

        event.getPickData().cast(LGInteractableTypes.PLAYER_VOTABLE)
                .ifPresent(p -> onVoteRemoved(event, p));
    }

    private void onVoteRemoved(LGPickRemovedEvent event, PickData<LGPlayer, Vote<LGPlayer>> pickData) {
        LGChatChannel channel = pickData.getEntry().getValue().getInfoMessagesChannel();

        String message = vote(player(pickData.getPicker().getName())) +
                         vote(" retire son vote ") +
                         ChatColor.ITALIC + "(" + player(ChatColor.ITALIC + pickData.getTarget().getName()) +
                         vote(ChatColor.ITALIC + ")");

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
