package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.google.inject.AbstractModule;

public final class LGKillModule extends AbstractModule {
    protected void configure() {
        bind(LGKillsOrchestrator.class).to(MinecraftLGKillsOrchestrator.class);
    }
}
