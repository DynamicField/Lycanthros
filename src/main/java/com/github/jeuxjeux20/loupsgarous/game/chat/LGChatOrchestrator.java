package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public interface LGChatOrchestrator extends LGGameOrchestratorDependent {
    void redirectMessage(LGPlayer sender, String message, String format);

    default void sendMessage(LGChatChannel channel, String message) {
        sendMessage(channel, Text.fromLegacy(message));
    }

    default void sendMessage(LGChatChannel channel, TextComponent message) {
        sendMessage(channel, p -> message);
    }

    void sendMessage(LGChatChannel channel, Function<? super LGPlayer, ? extends TextComponent> messageFunction);

    Set<LGChatChannel> getChannels();

    default Set<LGChatChannel> getWritableChannels(LGPlayer sender) {
        HashSet<LGChatChannel> channels = new HashSet<>();
        for (LGChatChannel channel : getChannels()) {
            if (channel.isWritable(sender)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    default void sendToEveryone(String message) {
        gameOrchestrator().getAllMinecraftPlayers().forEach(player -> player.sendMessage(message));
    }
}
