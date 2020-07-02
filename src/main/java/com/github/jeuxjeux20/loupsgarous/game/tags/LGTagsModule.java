package com.github.jeuxjeux20.loupsgarous.game.tags;

import com.github.jeuxjeux20.loupsgarous.game.tags.revealers.LGTagRevealersModule;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGTagsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGTagRevealersModule());

        install(new FactoryModuleBuilder()
                .implement(LGTagsOrchestrator.class, MinecraftLGTagsOrchestrator.class)
                .build(LGTagsOrchestrator.Factory.class));
    }
}
