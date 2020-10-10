package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.common.cache.CacheLoader;
import org.jetbrains.annotations.NotNull;

class ClassExtensionPointCacheLoader<T> extends
        CacheLoader<Class<? extends T>, ExtensionPoint<?>> {
    private final String prefix;

    public ClassExtensionPointCacheLoader(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public ExtensionPoint<?> load(@NotNull Class<? extends T> key) {
        return create(key);
    }

    private <C extends T> ExtensionPoint<?> create(Class<C> clazz) {
        String name = getExtensionPointName(clazz);

        return new ExtensionPoint<>(name);
    }

    protected <C extends T> String getExtensionPointName(Class<C> clazz) {
        return String.format("%s[%s]", prefix, clazz.getName());
    }
}
