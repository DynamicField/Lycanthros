package com.github.df.loupsgarous.storage;

public class FullStorageProperty<T>
        extends AbstractStorageProperty<T>
        implements StorageProperty.WithGetter<T>, StorageProperty.WithSetter<T> {
    public FullStorageProperty(StorageKey<T> key) {
        super(key);
    }
}
