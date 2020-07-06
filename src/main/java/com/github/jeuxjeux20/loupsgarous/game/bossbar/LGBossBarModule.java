package com.github.jeuxjeux20.loupsgarous.game.bossbar;

import com.google.inject.AbstractModule;

public final class LGBossBarModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGBossBarManager.class).to(MinecraftLGBossBarManager.class);
    }
}
