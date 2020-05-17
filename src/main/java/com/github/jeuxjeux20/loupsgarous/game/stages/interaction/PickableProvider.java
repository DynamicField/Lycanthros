package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.chat.GenericPickChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGGameChatChannel;

public interface PickableProvider {
    Pickable providePickable();

    default LGGameChatChannel getInfoMessagesChannel() {
        return new GenericPickChannel<>(this);
    }
}
