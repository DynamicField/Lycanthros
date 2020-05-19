package com.github.jeuxjeux20.loupsgarous;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * Modules inheriting this class will allow for module deduplication, which means
 * that this module will be {@linkplain #install(Module) installed} only once.
 */
public abstract class SingletonModule extends AbstractModule {
    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == getClass();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
