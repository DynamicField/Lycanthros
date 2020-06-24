package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.handler.LGStageInteractionHandlersModule;
import com.google.inject.AbstractModule;

public final class LGStageInteractionModule extends AbstractModule {
    protected void configure() {
        install(new LGStageInteractionHandlersModule());
    }
}
