package com.github.jeuxjeux20.loupsgarous.game.chat.listeners;

import com.github.jeuxjeux20.loupsgarous.ListenersModule;

public final class LGChatListenersModule extends ListenersModule {
    @Override
    protected void configureListeners() {
        addListener(TellPlayerCardListener.class);
        addListener(TellWinnerListener.class);
        addListener(TellPlayersKilledListener.class);
        addListener(TellPlayerVoteListener.class);
        addListener(RedirectChatMessageListener.class);
        addListener(TellPlayerVoteRemovedListener.class);
        addListener(TellStageTitleListener.class);
        addListener(TellVoteTipsListener.class);
    }
}
