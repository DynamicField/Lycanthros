package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.chat.listeners.LGChatListenersModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGChatModule extends ChatChannelsModule {
    @Override
    protected void configureBindings() {
        install(new LGChatListenersModule());

        install(new FactoryModuleBuilder()
                .implement(LGChatOrchestrator.class, MinecraftLGChatOrchestrator.class)
                .build(LGChatOrchestrator.Factory.class));

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
