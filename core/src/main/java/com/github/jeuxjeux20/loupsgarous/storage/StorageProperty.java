package com.github.jeuxjeux20.loupsgarous.storage;

public interface StorageProperty<T> {
    StorageKey<T> getKey();

    interface WithGetter<T> extends StorageProperty<T> {
        default T get(StorageProvider provider) {
            return provider.getStorage().get(getKey()).orElse(null);
        }
    }

    interface WithSetter<T> extends StorageProperty<T> {
        default void set(StorageProvider provider, T value) {
            provider.getStorage().put(getKey(), value);
        }
    }
}
