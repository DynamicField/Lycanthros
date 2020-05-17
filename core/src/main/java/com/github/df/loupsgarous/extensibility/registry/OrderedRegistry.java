package com.github.df.loupsgarous.extensibility.registry;

import com.github.df.loupsgarous.Registration;
import com.github.df.loupsgarous.event.registry.RegistryChangeEvent;
import com.github.jeuxjeux20.relativesorting.ElementSorter;
import com.github.jeuxjeux20.relativesorting.OrderedElement;
import com.github.jeuxjeux20.relativesorting.config.SortingConfiguration;
import com.github.jeuxjeux20.relativesorting.config.UnresolvableIdentifierHandling;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OrderedRegistry<T> implements Registry<T> {
    private static final SortingConfiguration SORTING_CONFIG = SortingConfiguration.builder()
            .unresolvableIdentifierHandling(UnresolvableIdentifierHandling.IGNORE)
            .build();

    private final Set<RegistryEntry<T>> allEntries = new LinkedHashSet<>();
    private final Map<String, RegistryEntry<T>> namedEntries = new HashMap<>();
    private final Map<T, RegistryEntry<T>> valueToEntry = new HashMap<>();

    private @Nullable ImmutableSet<RegistryEntry<T>> orderedEntries;
    private @Nullable ImmutableSet<T> orderedValues;

    @Override
    public Registration register(RegistryEntry<T> entry) {
        if (containsKey(entry.getName())) {
            throw new IllegalArgumentException(
                    "An entry with the name " + entry.getName() + " has already been registered.");
        }
        if (containsEntry(entry)) {
            throw new IllegalArgumentException(
                    "An entry of the same value has already been registered.");
        }

        allEntries.add(entry);
        if (entry.getName() != null) {
            namedEntries.put(entry.getName(), entry);
        }
        valueToEntry.put(entry.getValue(), entry);

        registryChanged();

        return new Registration() {
            @Override
            public void unregister() {
                if (isRegistered()) {
                    OrderedRegistry.this.unregister(entry);
                }
            }

            @Override
            public boolean isRegistered() {
                return allEntries.contains(entry);
            }
        };
    }

    @Override
    public void unregister(RegistryEntry<T> entry) {
        if (containsEntry(entry)) {
            allEntries.remove(entry);
            namedEntries.remove(entry.getName());
            valueToEntry.remove(entry.getValue());

            registryChanged();
        }
    }

    @Override
    public void unregister(T value) {
        unregister(valueToEntry.get(value));
    }

    @Override
    public void unregister(String name) {
        unregister(namedEntries.get(name));
    }

    @Override
    public Optional<T> get(String name) {
        return Optional.ofNullable(namedEntries.get(name).getValue());
    }

    @Override
    public Optional<RegistryEntry<T>> getEntry(String name) {
        return Optional.ofNullable(namedEntries.get(name));
    }

    @Override
    public Optional<RegistryEntry<T>> getEntry(T value) {
        return Optional.ofNullable(valueToEntry.get(value));
    }

    @Override
    public boolean containsEntry(RegistryEntry<T> entry) {
        return allEntries.contains(entry);
    }

    @Override
    public boolean containsKey(String name) {
        return namedEntries.containsKey(name);
    }

    @Override
    public boolean containsValue(T value) {
        return getValues().contains(value);
    }

    @Override
    public ImmutableSet<String> getNames() {
        return ImmutableSet.copyOf(namedEntries.keySet());
    }

    @Override
    public ImmutableSet<T> getValues() {
        return getOrderedValues();
    }

    @Override
    public ImmutableSet<RegistryEntry<T>> getEntries() {
        return getOrderedEntries();
    }

    private void registryChanged() {
        orderedEntries = null;
        orderedValues = null;

        Events.call(new RegistryChangeEvent(this));
    }

    private ImmutableSet<RegistryEntry<T>> getOrderedEntries() {
        if (orderedEntries == null) {
            ElementSorter<RegistryEntry<T>> itemSorter =
                    new ElementSorter<>(this::createOrderedElement);

            List<RegistryEntry<T>> items =
                    itemSorter.sort(ImmutableList.copyOf(allEntries), SORTING_CONFIG);

            orderedEntries = ImmutableSet.copyOf(items);
        }

        return orderedEntries;
    }

    private ImmutableSet<T> getOrderedValues() {
        if (orderedValues == null) {
            ImmutableSet<RegistryEntry<T>> entries = getOrderedEntries();

            orderedValues = entries.stream()
                    .map(RegistryEntry::getValue)
                    .collect(ImmutableSet.toImmutableSet());
        }

        return orderedValues;
    }

    private OrderedElement<? extends RegistryEntry<T>> createOrderedElement(RegistryEntry<T> x) {
        return new OrderedElement<>(x.getName(), x, x.getConstraints());
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return getValues().iterator();
    }
}
