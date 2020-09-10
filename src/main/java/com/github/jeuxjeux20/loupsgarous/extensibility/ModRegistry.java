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

    private final ModDescriptorRegistry descriptorRegistry;

    @Inject
    ModRegistry(ClassicGameMod classicGameMod, PatateMod patateMod,
                ModDescriptorRegistry descriptorRegistry) {
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
            notifyListeners(l -> l.onModAdded(mod));
        }
        return added;
    }

    public boolean removeMod(Mod mod) {
        boolean removed = mods.remove(mod);
        if (removed) {
            notifyListeners(l -> l.onModRemoved(mod));
        }
        return removed;
    }

    public ModDescriptorRegistry descriptors() {
        return descriptorRegistry;
    }

    public void addListener(ModRegistryListener listener) {
        registryListeners.add(listener);
    }

    public void removeListener(ModRegistryListener listener) {
        registryListeners.remove(listener);
    }

    private void notifyListeners(Consumer<ModRegistryListener> notifier) {
        for (ModRegistryListener registryListener : registryListeners) {
            notifier.accept(registryListener);
        }
    }
}
