package com.github.jeuxjeux20.loupsgarous.game.stages.listeners;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;

public final class LGStagesListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(LogStageEventsListener.class);
    }
}
