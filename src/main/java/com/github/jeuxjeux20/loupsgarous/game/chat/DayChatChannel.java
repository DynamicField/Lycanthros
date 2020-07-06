package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public class DayChatChannel implements LGChatChannel {
    @Override
    public String getName() {
        return "Jour";
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
        return sender.isAlive();
    }
}
