package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.chat.interceptor.LGChatChannelInterceptorsModule;
import com.github.jeuxjeux20.loupsgarous.game.chat.listeners.LGChatListenersModule;

public final class LGChatModule extends ChatChannelsModule {
    @Override
    protected void configureBindings() {
        install(new LGChatListenersModule());
        install(new LGChatChannelInterceptorsModule());

        bind(LGChatOrchestrator.class).to(MinecraftLGChatOrchestrator.class);
        bind(AnonymizedNamesProvider.class).to(RandomAnonymizedNamesProvider.class);
    }

    @Override
    protected void configureChatChannels() {
        addChatChannel(DayChatChannel.class);
        addChatChannel(DeadChatChannel.class);
        addChatChannel(LoupsGarousChatChannel.class);
        addChatChannel(OutOfGameChatChannel.class);
        addChatChannel(LoupsGarousVoteChatChannel.class);
    }
}
