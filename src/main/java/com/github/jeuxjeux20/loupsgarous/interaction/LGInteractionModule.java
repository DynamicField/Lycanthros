package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGComponents;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponentsModule;
import com.github.jeuxjeux20.loupsgarous.interaction.handler.LGInteractionHandlersModule;
import com.github.jeuxjeux20.loupsgarous.interaction.vote.LGInteractionVoteModule;
import com.google.inject.AbstractModule;

public final class LGInteractionModule extends AbstractModule {
    protected void configure() {
        install(new LGInteractionHandlersModule());
        install(new LGInteractionVoteModule());

        bind(InteractableRegistry.class);
        install(new OrchestratorComponentsModule() {
            @Override
            protected void configureOrchestratorComponents() {
                addOrchestratorComponent(LGComponents.INTERACTABLES,
                        InteractableRegistry.class);
            }
        });
    }
}
