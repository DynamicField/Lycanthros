package com.github.jeuxjeux20.loupsgarous.game.stages.descriptor;

import com.google.inject.AbstractModule;

public final class LGStagesDescriptorModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGStageDescriptorRegistry.class).to(MinecraftLGStageDescriptorRegistry.class);
        bind(LGStageDescriptorFinder.class).to(MinecraftLGStageDescriptorFinder.class);
    }
}
