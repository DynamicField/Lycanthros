package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.google.inject.Inject;

public class LoupsGarousVoteChatChannel extends LoupsGarousChatChannel {
    @Inject
    LoupsGarousVoteChatChannel(AnonymizedNamesProvider anonymizedNamesProvider) {
        super(anonymizedNamesProvider);
    }

}
