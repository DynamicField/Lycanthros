package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.OrderIdentifier;
import com.github.jeuxjeux20.loupsgarous.Order;
import com.github.jeuxjeux20.relativesorting.OrderConstraints;
import com.github.jeuxjeux20.relativesorting.OrderedElement;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Extension<T> {
    private final ExtensionPoint<T> extensionPoint;
    private final T value;
    private final @Nullable String identifier;
    private final OrderConstraints orderConstraints;

    private OrderedElement<Extension<T>> cachedOrderedElement;

    public Extension(ExtensionPoint<T> extensionPoint, T value) {
        this(extensionPoint, value,
                getIdentifierFromValue(value),
                getOrderConstraintsFromValue(value));
    }

    public Extension(ExtensionPoint<T> extensionPoint, T value,
                     @Nullable String identifier) {
        this(extensionPoint, value,
                identifier, getOrderConstraintsFromValue(value));
    }

    public Extension(ExtensionPoint<T> extensionPoint, T value,
                     OrderConstraints orderConstraints) {
        this(extensionPoint, value,
                getIdentifierFromValue(value), orderConstraints);
    }

    public Extension(ExtensionPoint<T> extensionPoint, T value,
                     @Nullable String identifier, OrderConstraints orderConstraints) {
        this.extensionPoint = extensionPoint;
        this.value = value;
        this.identifier = identifier;
        this.orderConstraints = orderConstraints;
    }

    public static ExtensionListBuilder listBuilder() {
        return new ExtensionListBuilder();
    }

    private static @Nullable String getIdentifierFromValue(Object value) {
        Objects.requireNonNull(value, "value is null");

        OrderIdentifier annotation = HasOrderingHint.getContainerIn(value).getAnnotation(OrderIdentifier.class);

        if (annotation != null) {
            return annotation.value();
        } else {
            return null;
        }
    }

    private static OrderConstraints getOrderConstraintsFromValue(Object value) {
        Objects.requireNonNull(value, "value is null");

        return Order.Util.toOrderConstraints(
                HasOrderingHint.getContainerIn(value).getAnnotation(Order.class)
        );
    }

    public ExtensionPoint<T> getExtensionPoint() {
        return extensionPoint;
    }

    public T getValue() {
        return value;
    }

    public @Nullable String getIdentifier() {
        return identifier;
    }

    OrderedElement<Extension<T>> getOrderedElement() {
        if (cachedOrderedElement != null) {
            return cachedOrderedElement;
        } else {
            return (cachedOrderedElement = new OrderedElement<>(identifier, this, orderConstraints));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Extension<?> extension = (Extension<?>) o;
        return Objects.equals(extensionPoint, extension.extensionPoint) &&
               Objects.equals(value, extension.value) &&
               Objects.equals(identifier, extension.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(extensionPoint, value, identifier);
    }
}
