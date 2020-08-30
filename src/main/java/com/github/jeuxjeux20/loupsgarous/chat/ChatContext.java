package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public final class ChatContext {
    private final LGGameOrchestrator orchestrator;
    private final LGPlayer player;

    public ChatContext(LGGameOrchestrator orchestrator, LGPlayer player) {
        this.orchestrator = orchestrator;
        this.player = player;
    }

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    public LGPlayer getPlayer() {
        return player;
    }
}
