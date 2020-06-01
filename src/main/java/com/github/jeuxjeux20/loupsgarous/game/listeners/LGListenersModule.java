package com.github.jeuxjeux20.loupsgarous.game.listeners;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;

public final class LGListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(SwitchTimeOfDayListener.class);
        addListener(TellPlayerCardListener.class);
        addListener(TellWinnerListener.class);
        addListener(TellPlayersKilledListener.class);
        addListener(TellPlayerVoteListener.class);
        addListener(ChasseurKillOnDeathListener.class);
        addListener(PlayerJoinGameListener.class);
        addListener(PlayerDiesOnKillListener.class);
        addListener(CheckWinConditionsListener.class);
        addListener(RedirectChatMessageListener.class);
        addListener(KillCoupleOnPartnerDeadListener.class);
        addListener(UpdateBossBarListener.class);
        addListener(PlayerJoinGameListener.class);
        addListener(DeadPlayerAsSpectatorListener.class);
        addListener(TellPlayerDevoteListener.class);
        addListener(ClearAllEffectsOnEndListener.class);
        addListener(TellStageTitleListener.class);
        addListener(TellVoteTipsListener.class);
        addListener(DeleteGamesOnDisableListener.class);
        addListener(ShortenVoteCountdownListener.class);
        addListener(PreventNightPlayerMovementListener.class);
    }
}
