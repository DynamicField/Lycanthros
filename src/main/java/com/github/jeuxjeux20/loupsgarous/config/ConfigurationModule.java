package com.github.jeuxjeux20.loupsgarous.config;

import com.google.inject.AbstractModule;

public final class ConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LGConfiguration.class).to(PluginLGConfiguration.class);
    }
}
