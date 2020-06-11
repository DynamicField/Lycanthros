package com.github.jeuxjeux20.loupsgarous.game.atmosphere;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class LGAtmosphereModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .build(VoteStructure.Factory.class));
    }
}
