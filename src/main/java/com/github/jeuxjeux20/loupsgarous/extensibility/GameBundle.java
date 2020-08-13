package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.relativesorting.ElementSorter;
import com.github.jeuxjeux20.relativesorting.OrderedElement;
import com.google.common.collect.*;

import java.util.*;

public final class GameBundle {
    private final ObjectFactory objectFactory;

    private final ImmutableSet<Extension<?>> extensions;
    private final ImmutableSet<ExtensionPoint<?>> extensionPoints;
    private final ImmutableMultimap<ExtensionPoint<?>, Extension<?>> extensionMap;
    private final ImmutableSetMultimap<ExtensionPoint<?>, Object> extensionContentsMap;
    private final Map<HandledExtensionPoint<?, ?>, ExtensionPointHandler> extensionPointHandlerMap =
            new HashMap<>();

    public GameBundle(Collection<Extension<?>> extensions, ObjectFactory objectFactory) {
        this.extensions = ImmutableSet.copyOf(extensions);
        this.objectFactory = objectFactory;
        this.extensionPoints = createExtensionPoints(this.extensions);
        this.extensionMap = createExtensionMap(this.extensions);
        this.extensionContentsMap = createExtensionContentsMap(this.extensions);
    }

    private ImmutableSet<ExtensionPoint<?>> createExtensionPoints(
            Set<Extension<?>> extensions) {
        return extensions.stream()
                .map(Extension::getExtensionPoint)
                .collect(ImmutableSet.toImmutableSet());
    }

    @SuppressWarnings("ConstantConditions") // It cannot be null, IntelliJ is tripping :v
    private ImmutableMultimap<ExtensionPoint<?>, Extension<?>> createExtensionMap(
            Set<Extension<?>> extensions) {
        return Multimaps.index(extensions, Extension::getExtensionPoint);
    }

    private ImmutableSetMultimap<ExtensionPoint<?>, Object> createExtensionContentsMap(
            Set<Extension<?>> extensions) {
        HashMultimap<ExtensionPoint<?>, Object> unsortedMap = extensions.stream().collect(
                Multimaps.flatteningToMultimap(
                        Extension::getExtensionPoint,
                        e -> e.getContents().stream(),
                        HashMultimap::create
                )
        );

        ImmutableSetMultimap.Builder<ExtensionPoint<?>, Object> builder =
                ImmutableSetMultimap.builder();

        unsortedMap.asMap().forEach((point, items) -> {
            // TODO: allow for more collections in relative-sorting
            List<Object> sortedItems = sort(ImmutableList.copyOf(items));
            builder.putAll(point, sortedItems);
        });

        return builder.build();
    }

    public ImmutableSet<Extension<?>> extensions() {
        return extensions;
    }

    public ImmutableCollection<Extension<?>> extensions(ExtensionPoint<?> extensionPoint) {
        return extensionMap.get(extensionPoint);
    }

    public ImmutableSet<ExtensionPoint<?>> extensionPoints() {
        return extensionPoints;
    }

    @SuppressWarnings("unchecked")
    public <T> ImmutableSet<T> contents(ExtensionPoint<T> extensionPoint) {
        return (ImmutableSet<T>) extensionContentsMap.get(extensionPoint);
    }

    @SuppressWarnings("unchecked")
    public <H extends ExtensionPointHandler> H handler(HandledExtensionPoint<?, H> extensionPoint) {
        return (H) extensionPointHandlerMap.computeIfAbsent(extensionPoint,
                point -> objectFactory.create(point.getHandlerClass()));
    }

    private <T> List<T> sort(List<T> items) {
        ElementSorter<T> elementSorter = new ElementSorter<>(item -> {
            if (item instanceof Class<?>) {
                return OrderedElement.fromType((Class<?>) item, item);
            }
            return OrderedElement.fromType(item.getClass(), item);
        });

        return elementSorter.sort(items);
    }
}
