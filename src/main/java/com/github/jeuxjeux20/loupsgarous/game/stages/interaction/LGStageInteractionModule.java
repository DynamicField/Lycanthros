package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public final class LGStageInteractionModule extends AbstractModule {
    protected void configure() {
        bind(new TypeLiteral<CommandPickHandler<PlayerPickable>>(){}).to(SinglePlayerCommandPickHandler.class);
        bind(new TypeLiteral<CommandPickHandler<CouplePickable>>(){}).to(CoupleCommandPickHandler.class);
    }
}
