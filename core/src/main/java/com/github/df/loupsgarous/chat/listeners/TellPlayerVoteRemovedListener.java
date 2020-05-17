package com.github.df.loupsgarous.chat.listeners;

import com.github.df.loupsgarous.UserFriendlyNamed;
import com.github.df.loupsgarous.chat.ChatChannel;
import com.github.df.loupsgarous.event.interaction.LGPickRemovedEvent;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.PickData;
import com.github.df.loupsgarous.interaction.vote.Vote;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.df.loupsgarous.chat.LGChatStuff.player;
import static com.github.df.loupsgarous.chat.LGChatStuff.vote;

public class TellPlayerVoteRemovedListener implements Listener {
    @EventHandler
    public void onPickRemoved(LGPickRemovedEvent event) {
        if (!event.isOrganic()) {
            return;
        }

        PickData<?> pickData = event.getPickData();

        if (pickData.getSource() instanceof Vote<?>) {
            ChatChannel channel = pickData.getSource().getInfoMessagesChannel();

            LGPlayer picker = pickData.getPicker();
            Object target = pickData.getTarget();

            String pickerName = picker.getName();
            String targetName = UserFriendlyNamed.get(target);

            String message = vote(player(pickerName)) +
                             vote(" retire son vote ") +
                             ChatColor.ITALIC + "(" + player(ChatColor.ITALIC + targetName) +
                             vote(ChatColor.ITALIC + ")");

            event.getOrchestrator().chat().sendMessage(channel, message);
        }
    }
}
