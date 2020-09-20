package com.github.jeuxjeux20.loupsgarous;

import me.lucko.helper.metadata.MetadataKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface MetadataExtension<T extends MetadataContainer, R> {
    default R get(T container) {
        return container.metadata().getOrPut(getKey(), this::getDefault);
    }

    default void set(T container, R value) {
        container.metadata().put(getKey(), value);
    }

    MetadataKey<R> getKey();

    R getDefault();

    static <T extends MetadataContainer, R>
    MetadataExtension<T, @Nullable R> create(MetadataKey<R> key) {
        return create(key, (R) null);
    }

    static <T extends MetadataContainer, R>
    MetadataExtension<T, R> create(MetadataKey<R> key, R defaultValue) {
        return new MetadataExtension<T, R>() {
            @Override
            public MetadataKey<R> getKey() {
                return key;
            }

            @Override
            public R getDefault() {
                return defaultValue;
            }
        };
    }

    static <T extends MetadataContainer, R>
    MetadataExtension<T, R> create(MetadataKey<R> key, Supplier<R> defaultValueSupplier) {
        return new MetadataExtension<T, R>() {
            @Override
            public MetadataKey<R> getKey() {
                return key;
            }

            @Override
            public R getDefault() {
                return defaultValueSupplier.get();
            }
        };
    }
}
