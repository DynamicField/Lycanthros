package com.github.df.loupsgarous.config;

public interface ConfigurationFile<T> {
    T get();

    void save();

    void reload();
}
