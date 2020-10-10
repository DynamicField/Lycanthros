package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.relativesorting.OrderConstraints;
import com.github.jeuxjeux20.relativesorting.OrderedElement;
import com.google.common.base.MoreObjects;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class Extension<T> {
    private final ExtensionPoint<T> extensionPoint;
    private final T value;
    private final ExtensionMetadata extensionMetadata;

    public Extension(ExtensionPoint<T> extensionPoint, T value) {
        this(extensionPoint, value, new ExtensionMetadata());
    }

    public Extension(ExtensionPoint<T> extensionPoint, T value, ExtensionMetadata extensionMetadata) {
        this.extensionPoint = extensionPoint;
        this.value = value;
        this.extensionMetadata = extensionMetadata;
    }

    public static <T> Builder<T> builder(ExtensionPoint<T> extensionPoint, T value) {
        return new Builder<>(extensionPoint, value);
    }

    public static ExtensionListBuilder listBuilder() {
        return new ExtensionListBuilder();
    }

    public ExtensionPoint<T> getExtensionPoint() {
        return extensionPoint;
    }

    public T getValue() {
        return value;
    }

    public ExtensionMetadata getExtensionMetadata() {
        return extensionMetadata;
    }

    OrderedElement<Extension<T>> getOrderedElement() {
        return new OrderedElement<>(
                extensionMetadata.getIdentifier(), this, extensionMetadata.getOrderConstraints());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Extension<?> extension = (Extension<?>) o;
        return Objects.equals(extensionPoint, extension.extensionPoint) &&
               Objects.equals(value, extension.value) &&
               Objects.equals(extensionMetadata, extension.extensionMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensionPoint, value, extensionMetadata);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("extensionPoint", extensionPoint)
                .add("value", value)
                .add("extensionMetadata", extensionMetadata)
                .toString();
    }

    public static final class Builder<T> {
        private final ExtensionPoint<T> extensionPoint;
        private final T value;
        private @Nullable String id;
        private final Set<String> before = new HashSet<>();
        private final Set<String> after = new HashSet<>();
        private int position;

        public Builder(ExtensionPoint<T> extensionPoint, T value) {
            this.extensionPoint = extensionPoint;
            this.value = value;
        }

        public Builder<T> id(@Nullable String id) {
            this.id = id;
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

        public Extension<T> build() {
            ExtensionMetadata meta =
                    new ExtensionMetadata(id, new OrderConstraints(before, after, position));

            return new Extension<>(extensionPoint, value, meta);
        }
    }
}
