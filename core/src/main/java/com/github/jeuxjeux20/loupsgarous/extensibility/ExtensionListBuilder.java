package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collector;

public final class ExtensionListBuilder {
    private final List<Extension<?>> extensions = new ArrayList<>();

    public <T> ExtensionListBuilder extend(ExtensionPoint<T> extensionPoint,
                                           Consumer<? super SubBuilder<T>> builderConsumer) {
        SubBuilder<T> subBuilder = new SubBuilder<>(extensionPoint);
        builderConsumer.accept(subBuilder);

        for (Extension.Builder<T> extensionBuilder : subBuilder.extensionBuilders) {
            extensions.add(extensionBuilder.build());
        }

        return this;
    }

    public <T> ExtensionListBuilder extendSingle(ExtensionPoint<T> extensionPoint, T value) {
        extensions.add(new Extension<>(extensionPoint, value));

        return this;
    }

    public <T> ExtensionListBuilder extendSingle(
            ExtensionPoint<T> extensionPoint, T value, ExtensionMetadata metadata) {
        extensions.add(new Extension<>(extensionPoint, value, metadata));

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

    public final static class SubBuilder<T> {
        private final ExtensionPoint<T> extensionPoint;
        private final List<Extension.Builder<T>> extensionBuilders = new ArrayList<>();

        private SubBuilder(ExtensionPoint<T> extensionPoint) {
            this.extensionPoint = extensionPoint;
        }

        public Extension.Builder<T> add(T value) {
            Extension.Builder<T> builder = new Extension.Builder<>(extensionPoint, value);
            extensionBuilders.add(builder);
            return builder;
        }
    }
}
