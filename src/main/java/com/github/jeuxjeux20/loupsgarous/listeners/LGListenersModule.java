package com.github.jeuxjeux20.loupsgarous.listeners;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;

public final class LGListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(GreetPlayerOnJoinListener.class);
        addListener(CheckWinConditionsListener.class);
        addListener(GreetPlayerOnJoinListener.class);
        addListener(ShortenVoteCountdownListener.class);
    }
}
