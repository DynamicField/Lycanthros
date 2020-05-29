package com.github.jeuxjeux20.loupsgarous.config;

import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface LGConfiguration {
    Optional<String> getDefaultWorld();

    void setDefaultWorld(@Nullable String defaultWorld);

    WorldPoolConfiguration getWorldPool();

    void setWorldPool(WorldPoolConfiguration worldPool);
}
