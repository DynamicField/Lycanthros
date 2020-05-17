package com.github.df.loupsgarous.extensibility;

import com.github.df.loupsgarous.event.extensibility.ModAddedEvent;
import com.github.df.loupsgarous.event.extensibility.ModRemovedEvent;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.Terminable;

import java.util.*;
import java.util.stream.Collectors;

public final class GameModsContainer implements Terminable {
    private final LGGameOrchestrator orchestrator;
    private final ModRegistry modRegistry;

    private final Set<Mod> mods = new HashSet<>();
    private final BiMap<ModEntry, Mod> modSourceMap = HashBiMap.create();
    private final ModRegistryListener modRegistryListener;

    public GameModsContainer(LGGameOrchestrator orchestrator,
            ModRegistry modRegistry) {
        this.orchestrator = orchestrator;
        this.modRegistry = modRegistry;
        this.modRegistryListener = new ModRegistryListener() {
            @Override
            public void onModRemoved(ModEntry entry) {
                if (isReactiveToModChanges()) {
                    Mod mod = modSourceMap.get(entry);
                    if (mod != null) {
                        removeMods(mod);
                    }
                }
            }

            @Override
            public void onModAdded(ModEntry entry) {
                if (isReactiveToModChanges()) {
                    Mod mod = entry.getModFactory().create(orchestrator);
                    modSourceMap.put(entry, mod);
                    addMods(mod);
                }
            }
        };

        modRegistry.addListener(modRegistryListener);
    }

    public void enable(Mod mod) {
        ensureContained(mod);
        orchestrator.logger().fine("Enabling mod " + mod);
        mod.setEnabled(true);
    }

    public void disable(Mod mod) {
        ensureContained(mod);
        orchestrator.logger().fine("Disabling mod " + mod);
        mod.setEnabled(false);
    }

    public void toggle(Mod mod) {
        ensureContained(mod);
        mod.setEnabled(!mod.isEnabled());
    }

    public ImmutableList<Mod> getMods() {
        return ImmutableList.copyOf(mods);
    }

    public void addMods(Mod... mods) {
        addMods(Arrays.asList(mods));
    }

    public void addMods(Collection<? extends Mod> mods) {
        orchestrator.logger().fine("Adding mods: " + formatForLogging(mods));
        for (Mod mod : mods) {
            boolean newlyAdded = this.mods.add(mod);
            if (newlyAdded && mod.getDescriptor().isEnabledByDefault()) {
                enable(mod);
            }

            Events.call(new ModAddedEvent(orchestrator, mod));
        }
    }

    public void removeMods(Mod... mods) {
        removeMods(Arrays.asList(mods));
    }

    public void removeMods(Collection<? extends Mod> mods) {
        orchestrator.logger().fine("Removing mods: " + formatForLogging(mods));
        for (Mod mod : mods) {
            if (!this.mods.contains(mod)) {
                continue;
            }

            mod.deactivate();
            this.mods.remove(mod);
            this.modSourceMap.inverse().remove(mod);

            Events.call(new ModRemovedEvent(orchestrator, mod));
        }
    }

    private boolean isReactiveToModChanges() {
        return orchestrator.allowsJoin();
    }

    private String formatForLogging(Collection<?> collection) {
        return "[" + collection.stream()
                .map(Objects::toString)
                .collect(Collectors.joining(", ")) + "]";
    }

    private void ensureContained(Mod mod) {
        if (!mods.contains(mod)) {
            throw new IllegalArgumentException("The given mod is not present in the container.");
        }
    }

    @Override
    public void close() {
        modRegistry.removeListener(modRegistryListener);
    }
}
