package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.chat.listeners.LGChatListenersModule;
import com.google.inject.AbstractModule;

public final class LGChatModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGChatListenersModule());
    }
}
