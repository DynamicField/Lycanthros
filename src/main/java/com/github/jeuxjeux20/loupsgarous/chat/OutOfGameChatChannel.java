package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;

public class OutOfGameChatChannel extends AbstractLGChatChannel {
    @Inject
    protected OutOfGameChatChannel(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public String getName() {
        return "Chat";
    }

    @Override
    public boolean isNameDisplayed() {
        return false;
    }

    private boolean isAccessible() {
        return !orchestrator.isGameRunning();
    }

    @Override
    public boolean isReadable(LGPlayer recipient) {
        return isAccessible();
    }

    @Override
    public boolean isWritable(LGPlayer sender) {
        return isAccessible();
    }
}
