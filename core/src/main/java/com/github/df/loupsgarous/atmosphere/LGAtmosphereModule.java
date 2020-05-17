package com.github.df.loupsgarous.atmosphere;

import com.github.df.loupsgarous.atmosphere.listeners.LGAtmosphereListenersModule;
import com.google.inject.AbstractModule;

public final class LGAtmosphereModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGAtmosphereListenersModule());
    }
}
