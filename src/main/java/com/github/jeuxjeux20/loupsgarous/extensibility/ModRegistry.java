package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.*;
import java.util.function.Consumer;

@Singleton
public class ModRegistry {
    private final Set<Mod> mods = new LinkedHashSet<>();
    private final List<ModRegistryListener> registryListeners = new ArrayList<>();

    private final ModDescriptor.Registry descriptorRegistry;

    @Inject
    ModRegistry(ClassicGameMod classicGameMod, PatateMod patateMod, ModDescriptor.Registry descriptorRegistry) {
        this.descriptorRegistry = descriptorRegistry;

        addMod(classicGameMod);
        addMod(patateMod);
    }

    public ImmutableSet<Mod> getMods() {
        return ImmutableSet.copyOf(mods);
    }

    public boolean addMod(Mod mod) {
        boolean added = mods.add(mod);
        if (added) {
            notifyRegistryListeners(l -> l.onModAdded(mod));
        }
        return added;
    }

    public boolean removeMod(Mod mod) {
        boolean removed = mods.remove(mod);
        if (removed) {
            notifyRegistryListeners(l -> l.onModRemoved(mod));
        }
        return removed;
    }

    public ModBundle createDefaultBundle() {
        ArrayList<Mod> mods = new ArrayList<>();

        for (Mod mod : this.mods) {
            ModDescriptor descriptor = descriptorRegistry.get(mod.getClass());

            if (descriptor.isEnabledByDefault()) {
                mods.add(mod);
            }
        }

        return new ModBundle(mods);
    }

    public ModDescriptor.Registry descriptors() {
        return descriptorRegistry;
    }

    public void addRegistryListener(ModRegistryListener listener) {
        registryListeners.add(listener);
    }

    public void removeRegistryListener(ModRegistryListener listener) {
        registryListeners.remove(listener);
    }

    private void notifyRegistryListeners(Consumer<ModRegistryListener> notifier) {
        for (ModRegistryListener registryListener : registryListeners) {
            notifier.accept(registryListener);
        }
    }
}
