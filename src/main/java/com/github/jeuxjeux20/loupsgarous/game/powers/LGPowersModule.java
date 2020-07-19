package com.github.jeuxjeux20.loupsgarous.game.powers;

import com.google.inject.AbstractModule;

public final class LGPowersModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGPowersOrchestrator.class).to(MinecraftLGPowersOrchestrator.class);
    }
}
