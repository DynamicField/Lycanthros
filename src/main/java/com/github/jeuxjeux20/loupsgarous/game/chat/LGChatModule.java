package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.github.jeuxjeux20.loupsgarous.game.chat.interceptor.LGChatChannelInterceptorsModule;
import com.github.jeuxjeux20.loupsgarous.game.chat.listeners.LGChatListenersModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGChatModule extends ChatChannelsModule {
    @Override
    protected void configureBindings() {
        install(new LGChatListenersModule());
        install(new LGChatChannelInterceptorsModule());

        bind(LGChatOrchestrator.class);

        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.CHAT, LGChatOrchestrator.class);
            }
        });

        install(new FactoryModuleBuilder()
                .implement(AnonymizedNamesProvider.class, RandomAnonymizedNamesProvider.class)
                .build(AnonymizedNamesProvider.Factory.class));
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
