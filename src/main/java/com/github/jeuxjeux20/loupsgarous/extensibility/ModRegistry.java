package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.Events;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Singleton
public class ModRegistry {
    private final Multimap<Plugin, Mod> mods = LinkedHashMultimap.create();
    private final List<ModRegistryListener> registryListeners = new ArrayList<>();

    private final ModDescriptorRegistry descriptorRegistry;

    @Inject
    ModRegistry(ClassicGameMod classicGameMod, PatateMod patateMod,
                LoupsGarous plugin,
                ModDescriptorRegistry descriptorRegistry) {
        this.descriptorRegistry = descriptorRegistry;

        add(plugin, classicGameMod);
        add(plugin, patateMod);

        Events.subscribe(PluginDisableEvent.class)
                .handler(e -> removeAll(e.getPlugin()))
                .bindWith(plugin);
    }

    public ImmutableSet<Mod> getMods() {
        return ImmutableSet.copyOf(mods.values());
    }

    public boolean add(Plugin plugin, Mod mod) {
        if (mods.containsValue(mod)) {
            return false;
        }

        mods.put(plugin, mod);
        notifyListeners(l -> l.onModAdded(mod));
        return true;
    }

    public boolean remove(Mod mod) {
        for (Map.Entry<Plugin, Mod> entry : mods.entries()) {
            if (entry.getValue() == mod) {
                remove(entry.getKey(), mod);
                return true;
            }
        }

        return false;
    }

    public void removeAll(Plugin plugin) {
        for (Mod mod : mods.get(plugin).toArray(new Mod[0])) {
            remove(plugin, mod);
        }
    }

    private void remove(Plugin plugin, Mod mod) {
        mods.remove(plugin, mod);
        notifyListeners(l -> l.onModRemoved(mod));
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
