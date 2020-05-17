package com.github.df.loupsgarous.chat;

import com.github.df.loupsgarous.chat.listeners.LGChatListenersModule;
import com.google.inject.AbstractModule;

public final class LGChatModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGChatListenersModule());
    }
}
