package com.github.jeuxjeux20.loupsgarous.game.bossbar;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGBossBarModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(LGBossBarManager.class, MinecraftLGBossBarManager.class)
                .build(LGBossBarManager.Factory.class));
    }
}
