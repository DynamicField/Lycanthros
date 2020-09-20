package com.github.jeuxjeux20.loupsgarous.config;

public interface ConfigurationFile<T> {
    T get();

    void save();

    void reload();
}
