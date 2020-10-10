package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.mechanic.Mechanic;
import com.github.jeuxjeux20.loupsgarous.mechanic.SpecificMechanicModifier;

public abstract class ChatChannelViewModifier
        extends SpecificMechanicModifier<ChatChannelViewRequest, ChatChannelView> {
    @Override
    protected final Mechanic<? extends ChatChannelViewRequest, ? extends ChatChannelView> getApplicableMechanic() {
        return ChatChannel.VIEW_MECHANIC;
    }
}
