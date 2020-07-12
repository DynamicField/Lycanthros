package com.github.jeuxjeux20.loupsgarous.game.chat;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.Nullable;

public abstract class ChatChannelsModule extends AbstractModule {
    private @Nullable Multibinder<LGChatChannel> chatChannelBinder;

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
        chatChannelBinder = Multibinder.newSetBinder(binder(), LGChatChannel.class);

        configureChatChannels();
    }

    protected final void addChatChannel(Class<? extends LGChatChannel> chatChannel) {
        addChatChannel(TypeLiteral.get(chatChannel));
    }

    protected final void addChatChannel(TypeLiteral<? extends LGChatChannel> chatChannel) {
        Preconditions.checkState(chatChannelBinder != null, "addChatChannel can only be used inside configureChatChannels()");

        chatChannelBinder.addBinding().to(chatChannel).in(OrchestratorScoped.class);
    }
}
