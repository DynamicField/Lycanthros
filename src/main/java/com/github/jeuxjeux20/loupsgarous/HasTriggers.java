package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.google.common.collect.ImmutableList;

public interface HasTriggers {
    default ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of();
    }
}
