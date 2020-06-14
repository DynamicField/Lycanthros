package com.github.jeuxjeux20.loupsgarous.game.actionbar;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class LGActionBarModule extends AbstractModule {
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(LGActionBarManager.class, MinecraftLGActionBarManager.class)
                .build(LGActionBarManager.Factory.class));
    }
}
