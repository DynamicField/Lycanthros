package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class LGKillModule extends AbstractModule {
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(LGKillsOrchestrator.class, MinecraftLGKillsOrchestrator.class)
                .build(LGKillsOrchestrator.Factory.class));
    }
}
