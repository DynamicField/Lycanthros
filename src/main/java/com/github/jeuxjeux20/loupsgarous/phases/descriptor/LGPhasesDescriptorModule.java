package com.github.jeuxjeux20.loupsgarous.phases.descriptor;

import com.google.inject.AbstractModule;

public final class LGPhasesDescriptorModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGPhaseDescriptor.Registry.class)
                .to(MinecraftLGPhaseDescriptorRegistry.class);

        bind(LGPhaseDescriptor.Factory.class)
                .to(MinecraftLGPhaseDescriptorFactory.class);
    }
}
