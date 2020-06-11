package com.github.jeuxjeux20.loupsgarous.game.atmosphere;

import com.github.jeuxjeux20.loupsgarous.game.listeners.LGListenersModule;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGAtmosphereModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGListenersModule());

        install(new FactoryModuleBuilder()
                .build(VoteStructure.Factory.class));
    }
}
