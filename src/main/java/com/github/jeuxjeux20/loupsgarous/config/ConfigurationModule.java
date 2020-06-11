package com.github.jeuxjeux20.loupsgarous.config;

import com.github.jeuxjeux20.loupsgarous.config.annotations.DefaultWorld;
import com.github.jeuxjeux20.loupsgarous.config.annotations.Pool;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public abstract class ConfigurationModule extends AbstractModule {
    @Override
    protected void configure() {
        bindConfiguration();
    }

    protected abstract void bindConfiguration();

    @Provides
    @DefaultWorld
    String provideDefaultWorld(LGConfiguration configuration) {
        return configuration.get().getDefaultWorld();
    }

    @Provides
    @Pool
    WorldPoolConfiguration providePool(LGConfiguration configuration) {
        return configuration.get().getWorldPool();
    }

    @Provides
    @Pool.MinWorlds
    int providePoolMinWorlds(@Pool WorldPoolConfiguration poolConfiguration) {
        return poolConfiguration.getMinWorlds();
    }

    @Provides
    @Pool.MaxWorlds
    int providePoolMaxWorlds(@Pool WorldPoolConfiguration poolConfiguration) {
        return poolConfiguration.getMaxWorlds().orElse(Integer.MAX_VALUE);
    }
}
