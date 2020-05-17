package com.github.df.loupsgarous.extensibility;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class ModEntry {
    private final Plugin owner;
    private final String name;
    private final NamespacedKey key;
    private final Mod.Factory modFactory;

    public ModEntry(Plugin owner, String name, Mod.Factory modFactory) {
        this.owner = owner;
        this.name = name;
        this.key = new NamespacedKey(owner, name);
        this.modFactory = modFactory;
    }

    public Plugin getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public Mod.Factory getModFactory() {
        return modFactory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ModEntry modEntry = (ModEntry) o;
        return Objects.equals(key, modEntry.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
