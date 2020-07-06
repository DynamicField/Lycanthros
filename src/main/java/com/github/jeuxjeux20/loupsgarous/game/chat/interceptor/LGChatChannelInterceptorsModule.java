package com.github.jeuxjeux20.loupsgarous.game.chat.interceptor;

import com.github.jeuxjeux20.loupsgarous.game.chat.LoupsGarousChatChannel;

public final class LGChatChannelInterceptorsModule extends ChatChannelInterceptorsModule {
    @Override
    protected void configure() {
        bindChannelInterceptor(LGMatchers.exactClass(LoupsGarousChatChannel.class), PetiteFilleSpyInterceptor.class);
    }
}
