package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.google.inject.AbstractModule;

public final class LGKillModule extends AbstractModule {
    protected void configure() {
        bind(LGKillsOrchestrator.class).to(MinecraftLGKillsOrchestrator.class);
        bind(PendingKillRegistry.class).to(MinecraftPendingKillRegistry.class);

        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.KILLS, MinecraftLGKillsOrchestrator.class);
            }
        });
    }
}
