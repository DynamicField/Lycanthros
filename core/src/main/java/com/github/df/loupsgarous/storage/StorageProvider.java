package com.github.df.loupsgarous.storage;

public interface StorageProvider {
    Storage getStorage();

    default <T> T getStored(StorageProperty.WithGetter<T> property) {
        return property.get(this);
    }

    default <T> void setStored(StorageProperty.WithSetter<? super T> property, T value) {
        property.set(this, value);
    }
}
