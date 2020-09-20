package com.github.jeuxjeux20.loupsgarous.descriptor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

public abstract class DescriptorRegistry<D extends Descriptor<T>, T> {
    private final LoadingCache<Class<? extends T>, D> descriptorCache;

    protected DescriptorRegistry() {
        this.descriptorCache = CacheBuilder.newBuilder().build(new DescriptorCacheLoader());
    }

    public D get(Class<? extends T> describedClass) {
        return descriptorCache.getUnchecked(describedClass);
    }

    public void invalidate(Class<? extends T> describedClass) {
        descriptorCache.invalidate(describedClass);
    }

    protected abstract D create(Class<? extends T> describedClass);

    private final class DescriptorCacheLoader extends CacheLoader<Class<? extends T>, D> {
        @Override
        public D load(@NotNull Class<? extends T> key) {
            return create(key);
        }
    }
}
