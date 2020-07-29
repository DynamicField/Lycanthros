package com.github.jeuxjeux20.loupsgarous.config;

import com.google.inject.AbstractModule;

public abstract class ConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        bindConfiguration();
    }

    protected abstract void bindConfiguration();
}
