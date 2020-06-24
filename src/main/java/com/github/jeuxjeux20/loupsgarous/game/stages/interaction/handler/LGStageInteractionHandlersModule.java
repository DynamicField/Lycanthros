package com.github.jeuxjeux20.loupsgarous.game.stages.interaction.handler;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Couple;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

public final class LGStageInteractionHandlersModule extends AbstractModule {
    protected void configure() {
        bind(new TypeLiteral<CommandPickHandler<Pickable<LGPlayer>>>(){}).to(SinglePlayerCommandPickHandler.class);
        bind(new TypeLiteral<CommandPickHandler<Pickable<Couple>>>(){}).to(CoupleCommandPickHandler.class);
    }
}
