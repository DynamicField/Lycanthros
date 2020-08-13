package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.inject.AbstractModule;

public final class LGExtensibilityModule extends AbstractModule {
    protected void configure() {
        bind(ModDescriptor.Factory.class).to(MinecraftModDescriptorFactory.class);
        bind(ModDescriptor.Registry.class).to(MinecraftModDescriptorRegistry.class);
    }
}
