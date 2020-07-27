package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public abstract class AbstractLGChatChannel implements LGChatChannel {
    protected final LGGameOrchestrator orchestrator;

    protected AbstractLGChatChannel(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public final LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }
}
