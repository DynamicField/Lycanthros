package com.github.jeuxjeux20.loupsgarous.phases.listeners;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;

public final class LGPhasesListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(LogPhaseEventsListener.class);
    }
}
