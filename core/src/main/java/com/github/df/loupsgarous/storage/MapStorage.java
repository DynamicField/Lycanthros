package com.github.df.loupsgarous.storage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapStorage implements Storage {
    private final Map<StorageKey<?>, Object> values = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(StorageKey<T> key) {
        return Optional.ofNullable((T) values.get(key));
    }

    @Override
    public boolean has(StorageKey<?> key) {
        return values.containsKey(key);
    }

    @Override
    public <T> void put(StorageKey<? super T> key, T item) {
        values.put(key, item);
    }
}
