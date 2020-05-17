package com.github.df.loupsgarous.storage;

public class ReadonlyStorageProperty<T>
        extends AbstractStorageProperty<T>
        implements StorageProperty.WithGetter<T> {
    public ReadonlyStorageProperty(StorageKey<T> key) {
        super(key);
    }
}
