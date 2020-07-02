package com.github.jeuxjeux20.loupsgarous.game.teams;

import com.github.jeuxjeux20.loupsgarous.game.teams.revealers.LGTeamRevealersModule;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGTeamsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGTeamRevealersModule());

        install(new FactoryModuleBuilder()
                .implement(LGTeamsOrchestrator.class, MinecraftLGTeamsOrchestrator.class)
                .build(LGTeamsOrchestrator.Factory.class));
    }
}
