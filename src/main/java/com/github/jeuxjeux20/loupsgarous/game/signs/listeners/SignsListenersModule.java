package com.github.jeuxjeux20.loupsgarous.game.signs.listeners;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;

public final class SignsListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(UpdateGameJoinSignListener.class);
        addListener(JoinOnSignClickListener.class);
    }
}
