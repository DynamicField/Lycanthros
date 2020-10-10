package com.github.jeuxjeux20.loupsgarous.chat;

import com.github.jeuxjeux20.loupsgarous.powers.PetiteFillePower;

public class PetiteFilleSpiesOnLoupsGarous extends ChatChannelViewModifier {
    @Override
    protected void execute(ChatChannelViewRequest request, ChatChannelView result) {
        if (request.getViewer().powers().has(PetiteFillePower.class) &&
            request.getChatChannel() == LGChatChannels.LOUPS_GAROUS) {
            result.setSenderAnonymized(true);
            result.setReadable(true);
        }
    }
}
