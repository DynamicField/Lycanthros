package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGChatModule extends ChatChannelsModule {
    @Override
    protected void configureBindings() {
        install(new FactoryModuleBuilder()
                .implement(LGChatManager.class, MinecraftLGChatManager.class)
                .build(LGChatManager.Factory.class));

        bind(AnonymizedNamesProvider.class).to(RandomAnonymizedNamesProvider.class);
    }

    @Override
    protected void configureChatChannels() {
        addChatChannel(DayChatChannel.class);
        addChatChannel(DeadChatChannel.class);
        addChatChannel(LoupsGarousChatChannel.class);
        addChatChannel(OutOfChatChannel.class);
        addChatChannel(LoupsGarousVoteChatChannel.class);
    }
}
