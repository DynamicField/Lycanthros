package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.chat.GenericPickChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.event.interaction.LGPickEventBase;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.condition.PickConditions;

public interface Pickable<T> {
    PickConditions<T> conditions();

    void pick(LGPlayer picker, T target);

    default boolean isMyEvent(LGPickEventBase<?, ?> event) {
        return event.getPickable() == this;
    }

    default LGChatChannel getInfoMessagesChannel() {
        return new GenericPickChannel<>(this);
    }
}
