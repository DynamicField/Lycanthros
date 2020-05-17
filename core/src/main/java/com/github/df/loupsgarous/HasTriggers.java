package com.github.df.loupsgarous;

import com.github.df.loupsgarous.event.LGEvent;
import com.google.common.collect.ImmutableList;

public interface HasTriggers {
    default ImmutableList<Class<? extends LGEvent>> getUpdateTriggers() {
        return ImmutableList.of();
    }
}
