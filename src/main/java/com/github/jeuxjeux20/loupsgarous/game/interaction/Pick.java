package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;

import java.util.Objects;
import java.util.Optional;

public final class Pick<T, P extends Pickable<T>> {
    private final InteractableEntry<P> entry;
    private final LGPlayer picker;
    private final T target;

    public Pick(InteractableEntry<P> entry, LGPlayer picker, T target) {
        this.entry = Objects.requireNonNull(entry, "entry is null");
        this.picker = Objects.requireNonNull(picker, "picker is null");
        this.target = Objects.requireNonNull(target, "target is null");
    }

    public InteractableEntry<P> getEntry() {
        return entry;
    }

    public LGPlayer getPicker() {
        return picker;
    }

    public T getTarget() {
        return target;
    }

    @SuppressWarnings("unchecked")
    public <NT, NP extends Pickable<NT>> Optional<Pick<NT, NP>> cast(TypeToken<NP> type) {
        if (type.isSupertypeOf(entry.getKey().getType())) {
            return Optional.of((Pick<NT, NP>) this);
        } else {
            return Optional.empty();
        }
    }

    public <NT, NP extends Pickable<NT>> Optional<Pick<NT, NP>> cast(Class<NP> clazz) {
        return cast(TypeToken.of(clazz));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pick<?, ?> pick = (Pick<?, ?>) o;
        return entry.equals(pick.entry) &&
               picker.equals(pick.picker) &&
               target.equals(pick.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry, picker, target);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("entry", entry)
                .add("picker", picker)
                .add("target", target)
                .toString();
    }
}