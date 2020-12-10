package com.github.jeuxjeux20.loupsgarous.extensibility.registry;

import com.github.jeuxjeux20.relativesorting.OrderConstraints;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RegistryEntry<T> {
    private final @Nullable String name;
    private final T value;
    private final OrderConstraints constraints;

    public RegistryEntry(@Nullable String name, T value, OrderConstraints constraints) {
        this.name = name;
        this.value = value;
        this.constraints = constraints;
    }

    public static <T> RegistryEntry<T> unnamed(T value) {
        return new RegistryEntry<>(null, value, OrderConstraints.EMPTY);
    }

    public static <T> RegistryEntry<T> named(String name, T value) {
        return new RegistryEntry<>(name, value, OrderConstraints.EMPTY);
    }

    public static <T> Builder<T> builder(T value) {
        return new Builder<>(value);
    }

    public @Nullable String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public OrderConstraints getConstraints() {
        return constraints;
    }

    public static final class Builder<T> {
        private final T value;
        private @Nullable String name;
        private final Set<String> before = new HashSet<>();
        private final Set<String> after = new HashSet<>();
        private int position;

        public Builder(T value) {
            this.value = value;
        }

        public Builder<T> name(@Nullable String name) {
            this.name = name;
            return this;
        }

        public Builder<T> locatedBefore(String... identifiers) {
            before.addAll(Arrays.asList(identifiers));
            return this;
        }

        public Builder<T> locatedAfter(String... identifiers) {
            after.addAll(Arrays.asList(identifiers));
            return this;
        }

        public Builder<T> orderPosition(int position) {
            this.position = position;
            return this;
        }

        public RegistryEntry<T> build() {
            return new RegistryEntry<>(name, value,
                    new OrderConstraints(before, after, position));
        }
    }
}
