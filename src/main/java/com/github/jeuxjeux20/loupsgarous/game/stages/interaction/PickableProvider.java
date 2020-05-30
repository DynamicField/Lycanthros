package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.chat.GenericPickChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;

public interface PickableProvider {
    Pickable providePickable();

    default LGChatChannel getInfoMessagesChannel() {
        return new GenericPickChannel<>(this);
    }
}
