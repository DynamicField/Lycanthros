package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.interaction.handler.LGInteractionHandlersModule;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGInteractionModule extends AbstractModule {
    protected void configure() {
        install(new LGInteractionHandlersModule());

        install(new FactoryModuleBuilder()
                .implement(InteractableRegistry.class, MinecraftInteractableRegistry.class)
                .build(InteractableRegistry.Factory.class));
    }
}
