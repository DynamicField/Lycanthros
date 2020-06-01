package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.google.common.collect.ImmutableList;

public interface HasTriggers {
    default ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of();
    }
}
