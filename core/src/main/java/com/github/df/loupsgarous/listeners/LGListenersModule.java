package com.github.df.loupsgarous.listeners;

import com.github.df.loupsgarous.ListenersModule;

public final class LGListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(GreetPlayerOnJoinListener.class);
        addListener(CheckWinConditionsListener.class);
        addListener(GreetPlayerOnJoinListener.class);
        addListener(ShortenVoteCountdownListener.class);
    }
}
