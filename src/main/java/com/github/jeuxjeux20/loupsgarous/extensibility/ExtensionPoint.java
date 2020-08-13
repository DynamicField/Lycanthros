package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.common.reflect.TypeToken;

import java.util.Arrays;
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

    @SafeVarargs
    public final Extension<T> extend(String name, T... values) {
        return new Extension<>(this, name, Arrays.asList(values));
    }

    public final Extension.Builder<T> extend(String name) {
        return Extension.builder(this, name);
    }

    public String getId() {
        return id;
    }

    public TypeToken<T> getValueType() {
        return valueType;
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
