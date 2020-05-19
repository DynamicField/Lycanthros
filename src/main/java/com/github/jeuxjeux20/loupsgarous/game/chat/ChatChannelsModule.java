package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class ChatChannelsModule extends AbstractModule {
    private @Nullable Multibinder<LGGameChatChannel> chatChannelBinder;

    @Override
    protected final void configure() {
        configureBindings();
        actualConfigureChatChannels();
    }

    protected void configureBindings() {
    }

    protected void configureChatChannels() {
    }

    private void actualConfigureChatChannels() {
        chatChannelBinder = Multibinder.newSetBinder(binder(), LGGameChatChannel.class);

        configureChatChannels();
    }

    protected final void addChatChannel(Class<? extends LGGameChatChannel> chatChannel) {
        addChatChannel(TypeLiteral.get(chatChannel));
    }

    protected final void addChatChannel(TypeLiteral<? extends LGGameChatChannel> chatChannel) {
        Preconditions.checkState(chatChannelBinder != null, "addChatChannel can only be used inside configureChatChannels()");

        chatChannelBinder.addBinding().to(chatChannel);
    }
}
