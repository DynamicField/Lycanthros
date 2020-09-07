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

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
