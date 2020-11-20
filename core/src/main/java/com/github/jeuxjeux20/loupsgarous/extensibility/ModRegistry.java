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

    @Inject
    ModRegistry(ClassicGameMod classicGameMod,
                LoupsGarous plugin) {

        register(classicGameMod, plugin);

        Events.subscribe(PluginDisableEvent.class)
                .handler(e -> unregisterAll(e.getPlugin()))
                .bindWith(plugin);
    }

    public ImmutableSet<Mod> getMods() {
        return ImmutableSet.copyOf(mods.values());
    }

    public boolean register(Mod mod, Plugin plugin) {
        if (mods.containsValue(mod)) {
            return false;
        }

        mods.put(plugin, mod);
        notifyListeners(l -> l.onModAdded(mod));
        return true;
    }

    public boolean unregister(Mod mod) {
        for (Map.Entry<Plugin, Mod> entry : mods.entries()) {
            if (entry.getValue() == mod) {
                unregister(entry.getKey(), mod);
                return true;
            }
        }

        return false;
    }

    public void unregisterAll(Plugin plugin) {
        for (Mod mod : mods.get(plugin).toArray(new Mod[0])) {
            unregister(plugin, mod);
        }
    }

    private void unregister(Plugin plugin, Mod mod) {
        mods.remove(plugin, mod);
        notifyListeners(l -> l.onModRemoved(mod));
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
