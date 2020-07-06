package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public class OutOfGameChatChannel implements LGChatChannel {
    @Override
    public String getName() {
        return "Chat";
    }

    @Override
    public boolean isNameDisplayed() {
        return false;
    }

    @Override
    public boolean isReadable(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return true;
    }

    @Override
    public boolean isWritable(LGPlayer sender, LGGameOrchestrator orchestrator) {
        return true;
    }
}
