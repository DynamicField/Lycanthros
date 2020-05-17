package com.github.df.loupsgarous.signs.listeners;

import com.github.df.loupsgarous.ListenersModule;

public final class SignsListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(UpdateGameJoinSignListener.class);
        addListener(JoinOnSignClickListener.class);
    }
}
