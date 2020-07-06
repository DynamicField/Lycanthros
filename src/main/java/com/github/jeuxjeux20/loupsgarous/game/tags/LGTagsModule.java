package com.github.jeuxjeux20.loupsgarous.game.tags;

import com.github.jeuxjeux20.loupsgarous.game.tags.revealers.LGTagRevealersModule;
import com.google.inject.AbstractModule;

public final class LGTagsModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGTagRevealersModule());

        bind(LGTagsOrchestrator.class).to(MinecraftLGTagsOrchestrator.class);
    }
}
