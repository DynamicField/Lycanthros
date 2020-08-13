package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.Plugin;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class MinecraftModDescriptorFactory implements ModDescriptor.Factory {
    private final Logger logger;

    @Inject
    MinecraftModDescriptorFactory(@Plugin Logger logger) {
        this.logger = logger;
    }

    @Override
    public ModDescriptor create(Class<? extends Mod> describedClass) {
        ModDescriptor descriptor = new ModDescriptor(describedClass);

        ModInfo annotation = describedClass.getAnnotation(ModInfo.class);
        if (annotation != null) {
            descriptor.setName(annotation.name());
            descriptor.setHidden(annotation.hidden());
            descriptor.setEnabledByDefault(annotation.enabledByDefault());

            try {
                ItemProvider itemProvider = annotation.item().getConstructor().newInstance();
                descriptor.setItem(itemProvider.get());
            } catch (Exception e) {
                logger.log(Level.WARNING,
                        "Couldn't use ItemProvider " + annotation.item(), e);
            }
        }

        return descriptor;
    }
}
