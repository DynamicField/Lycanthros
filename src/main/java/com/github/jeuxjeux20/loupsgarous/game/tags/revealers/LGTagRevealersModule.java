package com.github.jeuxjeux20.loupsgarous.game.tags.revealers;

public final class LGTagRevealersModule extends TagRevealersModule {
    @Override
    protected void configureBindings() {
        bind(TagRevealer.class).to(TagRevealerAggregator.class);
    }

    @Override
    protected void configureTagRevealers() {
        addTagRevealer(MaireTagRevealer.class);
    }
}
