package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.powers.PetiteFillePower;

public class PetiteFilleSpiesOnLoupsGarous implements ChatChannelViewTransformer {
    @Override
    public boolean handlesChannel(ChatChannel channel) {
        return channel == LGChatChannels.LOUPS_GAROUS;
    }

    @Override
    public void transform(ChatContext context, ChatChannelView view) {
        if (context.getPlayer().powers().has(PetiteFillePower.class)) {
            view.setSenderAnonymized(true);
            view.setReadable(true);
        }
    }
}
