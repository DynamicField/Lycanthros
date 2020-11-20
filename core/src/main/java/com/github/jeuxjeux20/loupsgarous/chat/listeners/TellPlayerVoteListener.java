package com.github.jeuxjeux20.loupsgarous.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.UserFriendlyNamed;
import com.github.jeuxjeux20.loupsgarous.chat.ChatChannel;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.PickData;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.Vote;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff.vote;

public class TellPlayerVoteListener implements Listener {
    @EventHandler
    public void onPick(LGPickEvent event) {
        PickData<?> pickData = event.getPickData();

        if (!(pickData.getSource() instanceof Vote<?>)) {
            return;
        }

        Vote<?> vote = (Vote<?>) pickData.getSource();
        ChatChannel channel = vote.getInfoMessagesChannel();

        LGPlayer picker = pickData.getPicker();
        Object target = pickData.getTarget();

        String pickerName = picker.getName();
        String targetName = UserFriendlyNamed.get(target);

        String message = vote(player(pickerName)) +
                         vote(" " + vote.getPointingText() + " ") +
                         vote(player(targetName));

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
