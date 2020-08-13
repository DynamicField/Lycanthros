package com.github.jeuxjeux20.loupsgarous.extensibility;

import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class AbstractMod implements Mod {
    protected void configureDefaults(ConfigurationNode configuration) {
    }

    @Override
    public final ConfigurationNode getDefaultConfiguration() {
        BasicConfigurationNode configuration = BasicConfigurationNode.root();

        configureDefaults(configuration);

        return configuration;
    }

    @SafeVarargs
    protected final <T> Extension<T> extend(ExtensionPoint<T> extensionPoint, T... contents) {
        return extensionPoint.extend(createExtensionName(extensionPoint), contents);
    }

    protected String createExtensionName(ExtensionPoint<?> extensionPoint) {
        return getClass().getSimpleName() + "_" + extensionPoint.getId();
    }
}
