package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.common.base.MoreObjects;
import com.google.common.reflect.TypeToken;

import java.util.Objects;
import java.util.Optional;

public final class PickData<T, P extends Pick<T>> {
    private final P source;
    private final LGPlayer picker;
    private final T target;

    public PickData(P source, LGPlayer picker, T target) {
        this.source = Objects.requireNonNull(source, "source is null");
        this.picker = Objects.requireNonNull(picker, "picker is null");
        this.target = Objects.requireNonNull(target, "target is null");
    }

    public P getSource() {
        return source;
    }

    public LGPlayer getPicker() {
        return picker;
    }

    public T getTarget() {
        return target;
    }

    @SuppressWarnings("unchecked")
    public <NT, NP extends Pick<? extends NT>> Optional<PickData<NT, ? extends NP>> cast(TypeToken<NP> type) {
        if (type.isSupertypeOf(source.getClass())) {
            return Optional.of((PickData<NT, ? extends NP>) this);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PickData<?, ?> pickData = (PickData<?, ?>) o;
        return source.equals(pickData.source) &&
               picker.equals(pickData.picker) &&
               target.equals(pickData.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, picker, target);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("entry", source)
                .add("picker", picker)
                .add("target", target)
                .toString();
    }
}
