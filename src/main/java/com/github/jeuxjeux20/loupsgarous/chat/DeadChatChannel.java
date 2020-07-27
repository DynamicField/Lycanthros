package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;

public class DeadChatChannel extends AbstractLGChatChannel {
    @Inject
    protected DeadChatChannel(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public String getName() {
        return "Morts";
    }

    @Override
    public boolean isNameDisplayed() {
        return true;
    }

    @Override
    public boolean isReadable(LGPlayer recipient) {
        return isWritable(recipient);
    }

    @Override
    public boolean isWritable(LGPlayer sender) {
        return sender.isDead() && orchestrator.isGameRunning();
    }
}
