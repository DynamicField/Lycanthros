package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.descriptor.AbstractDescriptorRegistry;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MinecraftModDescriptorRegistry
        extends AbstractDescriptorRegistry<ModDescriptor, Mod>
        implements ModDescriptor.Registry {

    @Inject
    MinecraftModDescriptorRegistry(ModDescriptor.Factory descriptorFactory) {
        super(descriptorFactory);
    }
}
