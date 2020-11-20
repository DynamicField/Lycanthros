package com.github.jeuxjeux20.loupsgarous.storage;

import java.util.Optional;
import java.util.function.Supplier;

public interface Storage {
    <T> Optional<T> get(StorageKey<T> key);

    default <T> T getOrPut(StorageKey<T> key, Supplier<? extends T> fallback) {
        Optional<T> value = get(key);
        if (value.isPresent()) {
            return value.get();
        } else {
            T newValue = fallback.get();
            put(key, newValue);
            return newValue;
        }
    }

    boolean has(StorageKey<?> key);

    <T> void put(StorageKey<? super T> key, T item);
}
