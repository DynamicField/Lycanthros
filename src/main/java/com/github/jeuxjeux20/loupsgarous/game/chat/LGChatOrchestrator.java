package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.VOTE_TIP_MESSAGE;

public interface LGChatOrchestrator extends LGGameOrchestratorComponent {
    void redirectMessage(LGPlayer sender, String message);

    default void sendMessage(LGChatChannel channel, String message) {
        sendMessage(channel, p -> message);
    }

    void sendMessage(LGChatChannel channel, Function<? super LGPlayer, String> messageFunction);

    Set<LGChatChannel> getChannels();

    default Set<LGChatChannel> getVisibleChannels(LGPlayer recipient) {
        HashSet<LGChatChannel> channels = new HashSet<>();
        for (LGChatChannel channel : getChannels()) {
            if (channel.cannotBeUsedByPlayer(gameOrchestrator())) continue;

            if (channel.areMessagesVisibleTo(recipient, gameOrchestrator())) {
                channels.add(channel);
            }
        }
        return Collections.unmodifiableSet(channels);
    }

    default Set<LGChatChannel> getWritableChannels(LGPlayer sender) {
        HashSet<LGChatChannel> channels = new HashSet<>();
        for (LGChatChannel channel : getChannels()) {
            if (channel.cannotBeUsedByPlayer(gameOrchestrator())) continue;

            if (channel.canTalk(sender, gameOrchestrator())) {
                channels.add(channel);
            }
        }
        return Collections.unmodifiableSet(channels);
    }

    default void sendVoteMessages(Votable votable) {
        sendMessage(votable.getInfoMessagesChannel(), VOTE_TIP_MESSAGE);
    }

    default void sendToEveryone(String message) {
        gameOrchestrator().getAllMinecraftPlayers().forEach(player -> player.sendMessage(message));
    }

    interface Factory {
        LGChatOrchestrator create(LGGameOrchestrator orchestrator);
    }
}
