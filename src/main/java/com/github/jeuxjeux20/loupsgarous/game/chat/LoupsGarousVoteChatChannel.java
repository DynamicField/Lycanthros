package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;

public class LoupsGarousVoteChatChannel extends LoupsGarousChatChannel {
    @Inject
    LoupsGarousVoteChatChannel(AnonymizedNamesProvider anonymizedNamesProvider) {
        super(anonymizedNamesProvider);
    }

    @Override
    public boolean areMessagesVisibleTo(LGPlayer recipient, LGGameOrchestrator orchestrator) {
        return hasAccess(recipient, true);
    }

    @Override
    public boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator) {
        return false;
    }
}
