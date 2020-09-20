package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Extension<T> {
    private final ExtensionPoint<T> extensionPoint;
    private final ImmutableList<T> contents;
    private final String name;

    public Extension(ExtensionPoint<T> extensionPoint, String name, Collection<T> contents) {
        this.extensionPoint = extensionPoint;
        this.contents = ImmutableList.copyOf(contents);
        this.name = name;
    }

    public static <T> Builder<T> builder(ExtensionPoint<T> extensionPoint, String name) {
        return new Builder<>(extensionPoint, name);
    }

    public ExtensionPoint<T> getExtensionPoint() {
        return extensionPoint;
    }

    public ImmutableList<T> getContents() {
        return contents;
    }

    public String getName() {
        return name;
    }

    public static final class Builder<T> {
        private final List<T> contents = new ArrayList<>();
        private final ExtensionPoint<T> extensionPoint;
        private final String name;

        public Builder(ExtensionPoint<T> extensionPoint, String name) {
            this.extensionPoint = extensionPoint;
            this.name = name;
        }

        public Builder<T> add(T content) {
            contents.add(content);
            return this;
        }

        public Extension<T> build() {
            return new Extension<>(extensionPoint, name, contents);
        }
    }
}
