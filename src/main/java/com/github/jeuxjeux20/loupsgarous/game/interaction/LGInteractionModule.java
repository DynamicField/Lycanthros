package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.github.jeuxjeux20.loupsgarous.game.interaction.handler.LGInteractionHandlersModule;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.LGInteractionVoteModule;
import com.google.inject.AbstractModule;

public final class LGInteractionModule extends AbstractModule {
    protected void configure() {
        install(new LGInteractionHandlersModule());
        install(new LGInteractionVoteModule());

        bind(InteractableRegistry.class).to(MinecraftInteractableRegistry.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.INTERACTABLES,
                        MinecraftInteractableRegistry.class);
            }
        });
    }
}
