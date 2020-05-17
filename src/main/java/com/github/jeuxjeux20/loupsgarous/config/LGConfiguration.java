package com.github.jeuxjeux20.loupsgarous.config;

import org.jetbrains.annotations.Nullable;

public interface LGConfiguration {
    @Nullable String getDefaultWorld();

    void setDefaultWorld(@Nullable String defaultWorld);
}
