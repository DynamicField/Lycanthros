package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.relativesorting.OrderConstraints;
import com.google.common.base.MoreObjects;
import org.jetbrains.annotations.Nullable;

public class ExtensionMetadata {
    private final @Nullable String identifier;
    private final OrderConstraints orderConstraints;

    public ExtensionMetadata() {
        this(null, OrderConstraints.EMPTY);
    }

    public ExtensionMetadata(OrderConstraints orderConstraints) {
        this(null, orderConstraints);
    }

    public ExtensionMetadata(@Nullable String identifier) {
        this(identifier, OrderConstraints.EMPTY);
    }

    public ExtensionMetadata(@Nullable String identifier, OrderConstraints orderConstraints) {
        this.identifier = identifier;
        this.orderConstraints = orderConstraints;
    }

    public @Nullable String getIdentifier() {
        return identifier;
    }

    public OrderConstraints getOrderConstraints() {
        return orderConstraints;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("identifier", identifier)
                .add("orderConstraints", orderConstraints)
                .toString();
    }
}
