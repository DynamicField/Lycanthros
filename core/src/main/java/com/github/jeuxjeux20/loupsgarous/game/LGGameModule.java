package com.github.jeuxjeux20.loupsgarous.game;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGGameModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGGameManager.class);

        install(new FactoryModuleBuilder()
                .implement(LGGameOrchestrator.class, MinecraftLGGameOrchestrator.class)
                .build(OrchestratorFactory.class));
    }
}
