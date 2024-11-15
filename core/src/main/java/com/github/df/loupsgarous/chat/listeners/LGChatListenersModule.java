package com.github.df.loupsgarous.chat.listeners;

import com.github.df.loupsgarous.ListenersModule;

public final class LGChatListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(TellPlayerCardListener.class);
        addListener(TellWinnerListener.class);
        addListener(TellPlayersKilledListener.class);
        addListener(TellPlayerVoteListener.class);
        addListener(TellPlayerVoteRemovedListener.class);
        addListener(TellPhaseTitleListener.class);
        addListener(TellVoteTipsListener.class);
    }
}
