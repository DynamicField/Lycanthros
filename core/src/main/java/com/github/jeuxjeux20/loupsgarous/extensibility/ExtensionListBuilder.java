package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.relativesorting.OrderConstraints;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collector;

public final class ExtensionListBuilder {
    private final List<Extension<?>> extensions = new ArrayList<>();

    public <T> ExtensionListBuilder extend(ExtensionPoint<T> extensionPoint,
                                           Consumer<SubBuilder<? super T>> builderConsumer) {
        SubBuilder<T> subBuilder = new SubBuilder<>(extensionPoint);
        builderConsumer.accept(subBuilder);
        extensions.addAll(subBuilder.extensions);

        return this;
    }

    public <T> ExtensionListBuilder extendSingle(ExtensionPoint<T> extensionPoint, T value) {
        extensions.add(new Extension<>(extensionPoint, value));

        return this;
    }

    public ExtensionListBuilder add(Extension<?> extension) {
        extensions.add(extension);

        return this;
    }

    public ExtensionListBuilder addAll(Collection<? extends Extension<?>> extensions) {
        this.extensions.addAll(extensions);

        return this;
    }

    public ImmutableList<Extension<?>> build() {
        return ImmutableList.copyOf(extensions);
    }

    public <T> T build(Collector<Extension<?>, ?, T> collector) {
        return extensions.stream().collect(collector);
    }

    public Extension<?>[] buildToArray() {
        return extensions.toArray(new Extension<?>[0]);
    }

    public final static class SubBuilder<T> {
        private final ExtensionPoint<T> extensionPoint;
        private final List<Extension<T>> extensions = new ArrayList<>();

        private SubBuilder(ExtensionPoint<T> extensionPoint) {
            this.extensionPoint = extensionPoint;
        }

        public SubBuilder<T> with(T value) {
            extensions.add(new Extension<>(extensionPoint, value));

            return this;
        }

        public SubBuilder<T> with(T value, String identifier) {
            extensions.add(new Extension<>(extensionPoint, value, identifier));

            return this;
        }

        public SubBuilder<T> with(T value, String identifier, OrderConstraints orderConstraints) {
            extensions.add(new Extension<>(extensionPoint, value, identifier, orderConstraints));

            return this;
        }

        public SubBuilder<T> with(T value, OrderConstraints orderConstraints) {
            extensions.add(new Extension<>(extensionPoint, value, orderConstraints));

            return this;
        }
    }
}
