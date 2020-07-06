package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;

public class LoupsGarousVoteChatChannel extends LoupsGarousChatChannel {
    @Inject
    LoupsGarousVoteChatChannel(AnonymizedNamesProvider anonymizedNamesProvider) {
        super(anonymizedNamesProvider);
    }

    @Override
    public boolean canBeUsedByPlayer(LGGameOrchestrator orchestrator) {
        return false;
    }
}
