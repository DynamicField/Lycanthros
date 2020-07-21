package com.github.jeuxjeux20.loupsgarous.game.powers;

import com.google.common.collect.ImmutableClassToInstanceMap;

import java.util.Optional;

public interface PowerRegistry {
    ImmutableClassToInstanceMap<LGPower> get();

    <T extends LGPower> Optional<T> get(Class<T> powerClass);

    <T extends LGPower> T getOrThrow(Class<T> powerClass);

    void put(LGPower power);

    boolean has(Class<? extends LGPower> powerClass);

    boolean remove(Class<? extends LGPower> powerClass);
}
