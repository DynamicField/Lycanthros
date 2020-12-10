package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.Events;
import org.bukkit.NamespacedKey;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Singleton
public class ModRegistry {
    private final Map<NamespacedKey, ModEntry> entries = new HashMap<>();
    private final List<ModRegistryListener> registryListeners = new ArrayList<>();

    @Inject
    ModRegistry(LoupsGarous plugin) {
        register(new ModEntry(plugin, "ClassicGameMod", ClassicGameMod::new));

        Events.subscribe(PluginDisableEvent.class)
                .handler(e -> unregisterAll(e.getPlugin()))
                .bindWith(plugin);
    }

    public ImmutableSet<ModEntry> getEntries() {
        return ImmutableSet.copyOf(entries.values());
    }

    public void register(ModEntry... entries) {
        for (ModEntry entry : entries) {
            register(entry);
        }
    }

    public void register(ModEntry entry) {
        if (entries.containsKey(entry.getKey())) {
            throw new IllegalArgumentException(
                    "The given entry has a key that is already present.");
        }

        entries.put(entry.getKey(), entry);
        notifyListeners(l -> l.onModAdded(entry));
    }

    public boolean unregister(NamespacedKey key) {
        ModEntry removedEntry = entries.remove(key);
        if (removedEntry != null) {
            notifyListeners(l -> l.onModRemoved(removedEntry));
            return true;
        } else {
            return false;
        }
    }

    public boolean unregister(ModEntry entry) {
        boolean removed = entries.remove(entry.getKey(), entry);
        if (removed) {
            notifyListeners(l -> l.onModRemoved(entry));
        }
        return removed;
    }

    public void unregisterAll(Plugin plugin) {
        entries.values().removeIf(entry -> entry.getOwner() == plugin);
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
