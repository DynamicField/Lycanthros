package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.AnonymousNameHolder;

public interface AnonymizedNamesProvider {
    default String createAnonymousNameOrGet(LGGameOrchestrator orchestrator, AnonymousNameHolder anonymousNameHolder,
                                            AnonymizedChatChannel chatChannel, String[] names) {
        if (anonymousNameHolder.getAnonymizedName() != null)
            return anonymousNameHolder.getAnonymizedName();

        return createAnonymousName(orchestrator, anonymousNameHolder, chatChannel, names);
    }

    String createAnonymousName(LGGameOrchestrator orchestrator, AnonymousNameHolder anonymousNameHolder,
                               AnonymizedChatChannel chatChannel, String[] names);
}
