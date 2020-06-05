package com.github.jeuxjeux20.loupsgarous.config;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface LGConfiguration {
    Optional<String> getDefaultWorld();

    void setDefaultWorld(@Nullable String defaultWorld);

    WorldPoolConfiguration getWorldPool();

    void setWorldPool(WorldPoolConfiguration worldPool);
}
