package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.interaction.handler.LGInteractionHandlersModule;
import com.github.jeuxjeux20.loupsgarous.game.interaction.vote.LGInteractionVoteModule;
import com.google.inject.AbstractModule;

public final class LGInteractionModule extends AbstractModule {
    protected void configure() {
        install(new LGInteractionHandlersModule());
        install(new LGInteractionVoteModule());

        bind(InteractableRegistry.class).to(MinecraftInteractableRegistry.class);
    }
}
