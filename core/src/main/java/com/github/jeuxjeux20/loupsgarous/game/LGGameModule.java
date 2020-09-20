package com.github.jeuxjeux20.loupsgarous.game;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public final class LGGameModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new OrchestratorScopeModule());

        bind(LGGameManager.class);
        bind(LGGameOrchestrator.class)
                .annotatedWith(Names.named("blankGame"))
                .to(MinecraftLGGameOrchestrator.class);

    }
}
