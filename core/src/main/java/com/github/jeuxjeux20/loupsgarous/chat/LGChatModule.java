package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.chat.listeners.LGChatListenersModule;
import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.google.inject.AbstractModule;

public final class LGChatModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGChatListenersModule());

        bind(LGChatOrchestrator.class);

        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.CHAT, LGChatOrchestrator.class);
            }
        });
    }
}
