package com.github.jeuxjeux20.loupsgarous.extensibility.registry;

import com.github.jeuxjeux20.loupsgarous.Registration;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public interface Registry<T> extends Iterable<T> {
    Registration register(RegistryEntry<T> entry);

    default Registration register(String name, T value) {
        return register(RegistryEntry.named(name, value));
    }

    default Registration register(T value) {
        return register(RegistryEntry.unnamed(value));
    }

    default Registration register(T value, Consumer<RegistryEntry.Builder<T>> entryConfigurator) {
        RegistryEntry.Builder<T> builder = RegistryEntry.builder(value);
        entryConfigurator.accept(builder);
        return register(builder.build());
    }

    default Terminable registerMany(Consumer<RegisterMany<T>> registerer) {
        RegisterMany<T> rMany = new RegisterMany<>();
        registerer.accept(rMany);

        List<RegistryEntry<T>> entries = rMany.buildAll();
        Registration[] registrations = new Registration[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            RegistryEntry<T> entry = entries.get(i);
            registrations[i] = register(entry);
        }

        return CompositeTerminable.create().withAll(registrations);
    }

    void unregister(@Nullable RegistryEntry<T> entry);

    void unregister(T value);

    void unregister(String name);

    Optional<T> get(String name);

    Optional<RegistryEntry<T>> getEntry(String name);

    Optional<RegistryEntry<T>> getEntry(T value);

    boolean containsEntry(RegistryEntry<T> entry);

    boolean containsKey(String name);

    boolean containsValue(T value);

    ImmutableSet<String> getNames();

    ImmutableSet<T> getValues();

    ImmutableSet<RegistryEntry<T>> getEntries();

    class RegisterMany<T> {
        private final List<RegistryEntry.Builder<T>> builders = new ArrayList<>();

        public RegistryEntry.Builder<T> register(T value) {
            RegistryEntry.Builder<T> builder = RegistryEntry.builder(value);
            builders.add(builder);
            return builder;
        }

        public List<RegistryEntry<T>> buildAll() {
            return builders.stream()
                    .map(RegistryEntry.Builder::build)
                    .collect(Collectors.toList());
        }
    }
}
