package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.common.cache.CacheLoader;
import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public abstract class ClassExtensionPointCacheLoader<T> extends
        CacheLoader<Class<? extends T>, ExtensionPoint<?>> {
    private final String suffix;

    public ClassExtensionPointCacheLoader(String suffix) {
        this.suffix = suffix;
    }

    public ClassExtensionPointCacheLoader() {
        this.suffix = "point";
    }

    @Override
    public ExtensionPoint<?> load(@NotNull Class<? extends T> key) {
        return create(key);
    }

    private <C extends T> ExtensionPoint<?> create(Class<C> clazz) {
        TypeToken<?> type = getExtensionPointType(clazz);
        String name = getExtensionPointName(clazz);

        return new ExtensionPoint<>(name, type);
    }

    protected <C extends T> String getExtensionPointName(Class<C> clazz) {
        return clazz + "_" + suffix;
    }

    protected abstract <C extends T> TypeToken<?> getExtensionPointType(Class<C> clazz);
}
