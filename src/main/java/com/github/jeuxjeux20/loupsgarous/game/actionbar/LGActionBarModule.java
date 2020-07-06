package com.github.jeuxjeux20.loupsgarous.game.actionbar;

import com.google.inject.AbstractModule;

public class LGActionBarModule extends AbstractModule {
    protected void configure() {
        bind(LGActionBarManager.class).to(MinecraftLGActionBarManager.class);
    }
}
