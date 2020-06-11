package com.github.jeuxjeux20.loupsgarous.game.listeners.atmosphere;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;

public final class LGAtmosphereListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(DeadPlayerAsSpectatorListener.class);
        addListener(PlayerDiesOnKillListener.class);
        addListener(PreventNightPlayerMovementListener.class);
    }
}
