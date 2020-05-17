package com.github.df.loupsgarous.storage;

public abstract class AbstractStorageProperty<T> implements StorageProperty<T> {
    private final StorageKey<T> key;

    protected AbstractStorageProperty(StorageKey<T> key) {
        this.key = key;
    }

    @Override
    public StorageKey<T> getKey() {
        return key;
    }
}
