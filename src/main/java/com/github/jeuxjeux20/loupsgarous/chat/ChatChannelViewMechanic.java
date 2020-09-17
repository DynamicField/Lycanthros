package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.Mechanic;

public interface ChatChannelViewMechanic extends Mechanic {
    boolean handlesChannel(ChatChannel channel);

    void execute(ChatChannelView view);
}
