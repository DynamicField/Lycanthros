package com.github.jeuxjeux20.loupsgarous.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.UserFriendlyNamed;
import com.github.jeuxjeux20.loupsgarous.chat.ChatChannel;
import com.github.jeuxjeux20.loupsgarous.event.interaction.LGPickEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.PickData;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.Vote;
import com.google.common.reflect.TypeToken;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.player;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.vote;

public class TellPlayerVoteListener implements Listener {
    @EventHandler
    public void onPick(LGPickEvent event) {
        event.getPickData().cast(new TypeToken<Vote<?>>() {}).ifPresent(p -> onVote(event, p));
    }

    private void onVote(LGPickEvent event, PickData<?, ? extends Vote<?>> pickData) {
        Vote<?> vote = pickData.getSource();
        ChatChannel channel = vote.getInfoMessagesChannel();

        LGPlayer picker = pickData.getPicker();
        Object target = pickData.getTarget();

        String pickerName = picker.getName();
        String targetName = UserFriendlyNamed.stringify(target);

        String message = vote(player(pickerName)) +
                         vote(" " + vote.getPointingText() + " ") +
                         vote(player(targetName));

        event.getOrchestrator().chat().sendMessage(channel, message);
    }
}
