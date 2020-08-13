package com.github.jeuxjeux20.loupsgarous.tags.revealers;

public final class LGTagRevealersModule extends TagRevealersModule {
    @Override
    protected void configureBindings() {

    }

    @Override
    protected void configureTagRevealers() {
        addTagRevealer(MaireTagRevealer.class);
    }
}
