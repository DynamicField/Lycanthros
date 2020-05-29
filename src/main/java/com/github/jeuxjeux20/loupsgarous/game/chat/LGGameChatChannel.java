package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface LGGameChatChannel {
    String getName();

    boolean isNameDisplayed();

    // This thing just feels weird for implementors, so it's disabled.
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator);

    boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator);

    boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator);

    default String formatUsername(LGPlayer sender, LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return sender.getName();
    }
}
