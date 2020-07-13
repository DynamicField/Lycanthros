package com.github.jeuxjeux20.loupsgarous.game.cards.distribution;

import com.google.inject.AbstractModule;

public final class LGCardDistributionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(CardDistributor.class).to(RandomCardDistributor.class);
    }
}
