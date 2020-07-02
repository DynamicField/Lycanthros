package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public final class InteractableEntry<T extends Interactable> {
    private final InteractableKey<T> key;
    private final T value;

    public InteractableEntry(InteractableKey<T> key, T value) {
        this.key = Objects.requireNonNull(key, "key is null");
        this.value = Objects.requireNonNull(value, "value is null");
    }

    public InteractableKey<T> getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InteractableEntry<?> that = (InteractableEntry<?>) o;
        return key.equals(that.key) &&
               value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("value", value)
                .toString();
    }
}
