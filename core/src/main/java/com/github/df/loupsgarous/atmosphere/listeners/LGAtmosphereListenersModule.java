package com.github.df.loupsgarous.atmosphere.listeners;

import com.github.df.loupsgarous.ListenersModule;

public final class LGAtmosphereListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(DeadPlayerAsSpectatorListener.class);
        addListener(PlayerDiesOnKillListener.class);
        addListener(PreventNightPlayerMovementListener.class);
        addListener(PutPlayerOnSpawnListener.class);
        addListener(SwitchTimeOfDayListener.class);
        addListener(PreventPlayerDamageListener.class);
    }
}
