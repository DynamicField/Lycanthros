package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.relativesorting.ElementSorter;
import com.github.jeuxjeux20.relativesorting.OrderedElement;
import com.github.jeuxjeux20.relativesorting.config.SortingConfiguration;
import com.github.jeuxjeux20.relativesorting.config.UnresolvableClassHandling;
import com.google.common.collect.*;

import java.util.*;

public final class GameBundle {
    private static final SortingConfiguration SORTING_CONFIGURATION =
            SortingConfiguration.builder()
                    .unresolvableClassHandling(UnresolvableClassHandling.IGNORE)
                    .build();

    private final ObjectFactory objectFactory;

    private final ImmutableSet<Extension<?>> extensions;
    private final ImmutableSetMultimap<ExtensionPoint<?>, Object> extensionContentsMap;
    private final Map<HandledExtensionPoint<?, ?>, ExtensionPointHandler> extensionPointHandlerMap =
            new HashMap<>();

    public GameBundle(Collection<Extension<?>> extensions, ObjectFactory objectFactory) {
        this.extensions = ImmutableSet.copyOf(extensions);
        this.objectFactory = objectFactory;
        this.extensionContentsMap = createExtensionContentsMap(this.extensions);
    }

    private ImmutableSetMultimap<ExtensionPoint<?>, Object> createExtensionContentsMap(
            Set<Extension<?>> extensions) {
        LinkedHashMultimap<ExtensionPoint<?>, Object> unsortedMap = LinkedHashMultimap.create();

        for (Extension<?> extension : extensions) {
            for (Object content : extension.getContents()) {
                unsortedMap.put(extension.getExtensionPoint(), content);
            }
        }

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

        return elementSorter.sort(items, SORTING_CONFIGURATION);
    }
}
