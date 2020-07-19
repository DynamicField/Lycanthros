package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.UserFriendlyNamed;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickRemovedEvent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.PickData;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.Vote;
import com.google.common.reflect.TypeToken;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteRemovedListener implements Listener {
    @EventHandler
    public void onPickRemoved(LGPickRemovedEvent event) {
        if (!event.isOrganic()) {
            return;
        }

        event.getPickData().cast(new TypeToken<Vote<?>>() {})
                .ifPresent(p -> onVoteRemoved(event, p));
    }

    private void onVoteRemoved(LGPickRemovedEvent event, PickData<?, ? extends Vote<?>> pickData) {
        LGChatChannel channel = pickData.getSource().getInfoMessagesChannel();

        LGPlayer picker = pickData.getPicker();
        Object target = pickData.getTarget();

        String pickerName = picker.getName();
        String targetName = UserFriendlyNamed.stringify(target);

        String message = vote(player(pickerName)) +
                         vote(" retire son vote ") +
                         ChatColor.ITALIC + "(" + player(ChatColor.ITALIC + targetName) +
                         vote(ChatColor.ITALIC + ")");

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
