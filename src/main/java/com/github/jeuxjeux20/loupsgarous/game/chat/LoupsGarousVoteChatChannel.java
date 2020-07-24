package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;

public class LoupsGarousVoteChatChannel extends LoupsGarousChatChannel {
    @Inject
    LoupsGarousVoteChatChannel(LGGameOrchestrator orchestrator,
                               AnonymizedNamesProvider.Factory anonymizedNamesProviderFactory) {
        super(orchestrator, anonymizedNamesProviderFactory);
    }

    @Override
    public boolean isWritable(LGPlayer sender) {
        return false;
    }
}
