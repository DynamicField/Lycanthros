package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;

import java.util.Objects;

public class ExtensionPoint<T> {
    private final String id;
    private final TypeToken<T> valueType;

    public ExtensionPoint(String id, Class<T> valueType) {
        this(id, TypeToken.of(valueType));
    }

    public ExtensionPoint(String id, TypeToken<T> valueType) {
        this.id = id;
        this.valueType = valueType;
    }

    public final Extension<T> extend(T value) {
        return new Extension<>(this, value);
    }

    public String getId() {
        return id;
    }

    public TypeToken<T> getValueType() {
        return valueType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("valueType", valueType)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtensionPoint<?> that = (ExtensionPoint<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
