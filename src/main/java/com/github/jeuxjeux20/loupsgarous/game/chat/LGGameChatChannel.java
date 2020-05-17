package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public interface LGGameChatChannel {
    String getName();

    boolean isNameDisplayed();

    boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator);

    boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator);

    boolean canTalk(LGPlayer sender, LGGameOrchestrator orchestrator);
}
