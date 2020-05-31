package com.github.jeuxjeux20.loupsgarous.signs;

import com.github.jeuxjeux20.loupsgarous.signs.listeners.SignsListenersModule;
import com.google.inject.AbstractModule;

public final class SignsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameJoinSignManager.class);

        install(new SignsListenersModule());
    }
}
