package com.github.df.loupsgarous.extensibility;

public interface ModRegistryListener {
    void onModRemoved(ModEntry entry);

    void onModAdded(ModEntry entry);
}
