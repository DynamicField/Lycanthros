package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.reflect.TypeToken;

/**
 * Represents a key that identifies an {@link Interactable}'s type.
 *
 * @param <T> the type of the pickable
 */
public final class InteractableKey<T extends Interactable> {
    private final String name;
    private final TypeToken<T> type;

    public InteractableKey(String name, TypeToken<T> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public TypeToken<T> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InteractableKey<?> that = (InteractableKey<?>) o;
        return Objects.equal(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("type", type)
                .toString();
    }
}
