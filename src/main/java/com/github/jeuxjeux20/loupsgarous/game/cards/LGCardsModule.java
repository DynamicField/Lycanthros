package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.LGCompositionModule;
import com.github.jeuxjeux20.loupsgarous.game.cards.revealers.LGCardRevealersModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGCardsModule extends CardsModule {
    @Override
    protected void configureBindings() {
        install(new LGCompositionModule());
        install(new LGCardRevealersModule());

        install(new FactoryModuleBuilder()
                .implement(LGCardsOrchestrator.class, MinecraftLGCardsOrchestrator.class)
                .build(LGCardsOrchestrator.Factory.class));
    }

    @Override
    protected void configureCards() {
        addCard(ChasseurCard.class);
        addCard(CupidonCard.class);
        addCard(LoupGarouCard.class);
        addCard(PetiteFilleCard.class);
        addCard(SorciereCard.class);
        addCard(VillageoisCard.class);
        addCard(VoyanteCard.class);
    }
}
