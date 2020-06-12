package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;

public final class LGListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(ChasseurKillOnDeathListener.class);
        addListener(GreetPlayerOnJoinListener.class);
        addListener(CheckWinConditionsListener.class);
        addListener(KillCoupleOnPartnerDeadListener.class);
        addListener(GreetPlayerOnJoinListener.class);
        addListener(ShortenVoteCountdownListener.class);
    }
}
