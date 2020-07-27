package com.github.jeuxjeux20.loupsgarous.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;

public final class LGChatListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(TellPlayerCardListener.class);
        addListener(TellWinnerListener.class);
        addListener(TellPlayersKilledListener.class);
        addListener(TellPlayerVoteListener.class);
        addListener(TellPlayerVoteRemovedListener.class);
        addListener(TellStageTitleListener.class);
        addListener(TellVoteTipsListener.class);
    }
}
