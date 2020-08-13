package com.github.jeuxjeux20.loupsgarous.extensibility;

import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public interface Mod {
    default ConfigurationNode getDefaultConfiguration() {
        return BasicConfigurationNode.root();
    }

    List<Extension<?>> createExtensions(ConfigurationNode configuration);
}
