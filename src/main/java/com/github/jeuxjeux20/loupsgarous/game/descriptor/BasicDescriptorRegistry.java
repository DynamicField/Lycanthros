package com.github.jeuxjeux20.loupsgarous.game.descriptor;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

public abstract class BasicDescriptorRegistry<D extends Descriptor<T>, T>
        implements DescriptorRegistry<D, T> {
    private final DescriptorFactory<D, T> descriptorFactory;

    private final LoadingCache<Class<? extends T>, D> descriptorCache;

    protected BasicDescriptorRegistry(DescriptorFactory<D, T> descriptorFactory) {
        this.descriptorFactory = descriptorFactory;
        this.descriptorCache = CacheBuilder.newBuilder().build(new DescriptorFactoryLoader());
    }

    public D get(Class<? extends T> describedClass) {
        return descriptorCache.getUnchecked(describedClass);
    }

    public void invalidate(Class<? extends T> describedClass) {
        descriptorCache.invalidate(describedClass);
    }

    private final class DescriptorFactoryLoader
            extends CacheLoader<Class<? extends T>, D> {
        @Override
        public D load(@NotNull Class<? extends T> key) {
            return descriptorFactory.create(key);
        }
    }
}
