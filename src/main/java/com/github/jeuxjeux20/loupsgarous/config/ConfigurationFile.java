package com.github.jeuxjeux20.loupsgarous.config;

import org.spongepowered.configurate.ConfigurationNode;

public interface ConfigurationFile<T> {
    T get();

    ConfigurationNode getNode();

    void save();

    void reload();
}
