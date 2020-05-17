package com.github.df.loupsgarous.storage;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public class StorageKey<T> {
    private final String name;

    public StorageKey(String name) {
        this.name = Objects.requireNonNull(name, "name is null");
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        StorageKey<?> that = (StorageKey<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .toString();
    }
}
