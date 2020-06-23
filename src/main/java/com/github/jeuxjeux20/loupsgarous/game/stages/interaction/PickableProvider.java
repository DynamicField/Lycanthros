package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.chat.GenericPickChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEventBase;

public interface PickableProvider<P extends Pickable<?>> {
    P providePickable();

    default boolean isMyEvent(LGPickEventBase<?, PickableProvider<P>> pickEvent) {
        return pickEvent.getPickableProvider() == this;
    }

    default LGChatChannel getInfoMessagesChannel() {
        return new GenericPickChannel<>(this);
    }
}
