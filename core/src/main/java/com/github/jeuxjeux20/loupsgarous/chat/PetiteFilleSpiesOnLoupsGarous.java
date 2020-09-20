package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.powers.PetiteFillePower;

public class PetiteFilleSpiesOnLoupsGarous implements ChatChannelViewMechanic {
    @Override
    public boolean handlesChannel(ChatChannel channel) {
        return channel == LGChatChannels.LOUPS_GAROUS;
    }

    @Override
    public void execute(ChatChannelView view) {
        if (view.getViewer().powers().has(PetiteFillePower.class)) {
            view.setSenderAnonymized(true);
            view.setReadable(true);
        }
    }
}
