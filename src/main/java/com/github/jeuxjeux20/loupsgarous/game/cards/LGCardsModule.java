package com.github.jeuxjeux20.loupsgarous.game.cards;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.LGCompositionModule;

public final class LGCardsModule extends CardsModule {
    @Override
    protected void configureBindings() {
        install(new LGCompositionModule());
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
