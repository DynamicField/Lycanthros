package com.github.df.loupsgarous.chat;

import com.github.df.loupsgarous.mechanic.Mechanic;
import com.github.df.loupsgarous.mechanic.SpecificMechanicModifier;

public abstract class ChatChannelViewModifier
        extends SpecificMechanicModifier<ChatChannelViewRequest, ChatChannelView> {
    @Override
    protected final Mechanic<? extends ChatChannelViewRequest, ? extends ChatChannelView> getApplicableMechanic() {
        return ChatChannel.VIEW_MECHANIC;
    }
}
