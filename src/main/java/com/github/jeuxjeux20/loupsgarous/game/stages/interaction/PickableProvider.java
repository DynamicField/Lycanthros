package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.chat.GenericPickChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.events.LGPickEvent;

public interface PickableProvider {
    Pickable providePickable();

    default boolean isMyEvent(LGPickEvent pickEvent) {
        return pickEvent.getPickableProvider() == this;
    }

    default LGChatChannel getInfoMessagesChannel() {
        return new GenericPickChannel<>(this);
    }
}
