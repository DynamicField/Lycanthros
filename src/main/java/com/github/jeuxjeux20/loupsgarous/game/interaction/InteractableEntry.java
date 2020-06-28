package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public final class InteractableEntry<T extends Interactable> {
    private final InteractableKey<? super T> key;
    private final T value;

    public InteractableEntry(InteractableKey<? super T> key, T value) {
        this.key = key;
        this.value = value;
    }

    public InteractableKey<? super T> getKey() {
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
        return Objects.equal(key, that.key) &&
               Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, value);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("key", key)
                .add("value", value)
                .toString();
    }
}
