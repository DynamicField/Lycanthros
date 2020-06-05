package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public final class OutOfChatChannel implements LGChatChannel {
    @Override
    public String getName() {
        return "Chat";
    }

    @Override
    public boolean isNameDisplayed() {
        return false;
    }

    @Override
    public boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator) {
        return !orchestrator.isGameRunning();
    }

    @Override
    public boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return true;
    }

    @Override
    public boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator) {
        return true;
    }
}