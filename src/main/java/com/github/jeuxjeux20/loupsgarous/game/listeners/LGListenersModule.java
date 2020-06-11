package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;
import com.github.jeuxjeux20.loupsgarous.game.listeners.atmosphere.LGAtmosphereListenersModule;

public final class LGListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        install(new LGAtmosphereListenersModule());

        addListener(SwitchTimeOfDayListener.class);
        addListener(TellPlayerCardListener.class);
        addListener(TellWinnerListener.class);
        addListener(TellPlayersKilledListener.class);
        addListener(TellPlayerVoteListener.class);
        addListener(ChasseurKillOnDeathListener.class);
        addListener(GreetPlayerOnJoinListener.class);
        addListener(CheckWinConditionsListener.class);
        addListener(RedirectChatMessageListener.class);
        addListener(KillCoupleOnPartnerDeadListener.class);
        addListener(UpdateBossBarListener.class);
        addListener(GreetPlayerOnJoinListener.class);
        addListener(TellPlayerDevoteListener.class);
        addListener(TellStageTitleListener.class);
        addListener(TellVoteTipsListener.class);
        addListener(DeleteGamesOnDisableListener.class);
        addListener(ShortenVoteCountdownListener.class);
        addListener(PutPlayerOnSpawnListener.class);
    }
}
