package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public class DeadChatChannel implements LGChatChannel {
    @Override
    public String getName() {
        return "Morts";
    }

    @Override
    public boolean isNameDisplayed() {
        return true;
    }

    @Override
    public boolean isReadable(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return isWritable(recipient, orchestrator);
    }

    @Override
    public boolean isWritable(LGPlayer sender, LGGameOrchestrator orchestrator) {
        return sender.isDead();
    }
}
