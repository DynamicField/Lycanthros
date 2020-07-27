package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.cards.composition.LGCardCompositionModule;
import com.github.jeuxjeux20.loupsgarous.cards.distribution.LGCardDistributionModule;
import com.github.jeuxjeux20.loupsgarous.cards.revealers.LGCardRevealersModule;

public final class LGCardsModule extends CardsModule {
    @Override
    protected void configureBindings() {
        install(new LGCardCompositionModule());
        install(new LGCardRevealersModule());
        install(new LGCardDistributionModule());
    }

    @Override
    protected void configureCards() {
        addCard(ChasseurCard.INSTANCE);
        addCard(CupidonCard.INSTANCE);
        addCard(LoupGarouCard.INSTANCE);
        addCard(PetiteFilleCard.INSTANCE);
        addCard(SorciereCard.INSTANCE);
        addCard(VillageoisCard.INSTANCE);
        addCard(VoyanteCard.INSTANCE);
    }
}
