package com.github.jeuxjeux20.loupsgarous.cards;

import com.github.jeuxjeux20.loupsgarous.cards.composition.LGCardCompositionModule;
import com.github.jeuxjeux20.loupsgarous.cards.distribution.LGCardDistributionModule;
import com.google.inject.AbstractModule;

public final class LGCardsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGCardCompositionModule());
        install(new LGCardDistributionModule());
    }
}
