package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.phases.listeners.LGPhasesListenersModule;
import com.google.inject.AbstractModule;

public final class LGPhasesModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGPhasesListenersModule());
    }
}
