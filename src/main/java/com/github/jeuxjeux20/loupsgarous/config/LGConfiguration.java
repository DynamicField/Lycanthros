package com.github.jeuxjeux20.loupsgarous.config;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface LGConfiguration {
    RootConfiguration get();

    void save();

    void reload();
}
