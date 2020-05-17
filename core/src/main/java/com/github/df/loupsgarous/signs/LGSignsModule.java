package com.github.df.loupsgarous.signs;

import com.github.df.loupsgarous.signs.listeners.SignsListenersModule;
import com.google.inject.AbstractModule;

public final class LGSignsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameJoinSignManager.class).to(PersistentDataContainerGameJoinSignManager.class);

        install(new SignsListenersModule());
    }
}
