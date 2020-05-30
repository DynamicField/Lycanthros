package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface LGChatChannel {
    String getName();

    boolean isNameDisplayed();

    boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator);

    default boolean cannotBeUsedByPlayer(LGGameOrchestrator orchestrator) {
        return !canBeUsedByPlayer(orchestrator);
    }

    boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator);

    boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator);

    default String formatUsername(LGPlayer sender, LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return sender.getName();
    }
}
