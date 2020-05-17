package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Votable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.VOTE_TIP_MESSAGE;

public interface LGGameChatManager {
    void redirectMessage(LGPlayer sender, String message, LGGameOrchestrator orchestrator);

    default void sendMessage(LGGameChatChannel channel, String message, LGGameOrchestrator orchestrator) {
        sendMessage(channel, p -> message, orchestrator);
    }

    void sendMessage(LGGameChatChannel channel, Function<? super LGPlayer, String> messageFunction,
                     LGGameOrchestrator orchestrator);

    Set<LGGameChatChannel> getChannels();

    default Set<LGGameChatChannel> getVisibleChannels(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        HashSet<LGGameChatChannel> channels = new HashSet<>();
        for (LGGameChatChannel channel : getChannels()) {
            if (!channel.canBeUsedByPlayer(orchestrator)) continue;

            if (channel.areMessagesVisibleTo(recipient, orchestrator)) {
                channels.add(channel);
            }
        }
        return Collections.unmodifiableSet(channels);
    }

    default Set<LGGameChatChannel> getWritableChannels(LGPlayer sender, LGGameOrchestrator orchestrator) {
        HashSet<LGGameChatChannel> channels = new HashSet<>();
        for (LGGameChatChannel channel : getChannels()) {
            if (!channel.canBeUsedByPlayer(orchestrator)) continue;

            if (channel.canTalk(sender, orchestrator)) {
                channels.add(channel);
            }
        }
        return Collections.unmodifiableSet(channels);
    }

    default void sendVoteMessages(Votable votable, LGGameOrchestrator orchestrator) {
        sendMessage(votable.getInfoMessagesChannel(), VOTE_TIP_MESSAGE, orchestrator);
    }
}
