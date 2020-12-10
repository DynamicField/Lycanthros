package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.text.TextComponent;

import java.util.function.Function;

public interface ChatOrchestrator {
    void sendMessage(ChatChannel channel,
            Function<? super LGPlayer, ? extends TextComponent> messageFunction);

    void sendMessage(ChatChannel channel, String message);

    void sendMessage(ChatChannel channel, TextComponent message);

    ImmutableSet<ChatChannel> getChannels();

    void sendToEveryone(String message);
}
