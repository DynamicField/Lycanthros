package com.github.jeuxjeux20.loupsgarous.game.chat;

public final class LGChatModule extends ChatChannelsModule {
    @Override
    protected void configureBindings() {
        bind(LGGameChatManager.class).to(DefaultLGGameChatManager.class);
        bind(AnonymizedNamesProvider.class).to(RandomAnonymizedNamesProvider.class);
    }

    @Override
    protected void configureChatChannels() {
        addChatChannel(DayChatChannel.class);
        addChatChannel(DeadChatChannel.class);
        addChatChannel(LoupsGarousChatChannel.class);
        addChatChannel(OutOfGameChatChannel.class);
        addChatChannel(LoupsGarousVoteChatChannel.class);
    }
}
