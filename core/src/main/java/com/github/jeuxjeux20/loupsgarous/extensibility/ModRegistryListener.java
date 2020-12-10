package com.github.jeuxjeux20.loupsgarous.extensibility;

public interface ModRegistryListener {
    void onModRemoved(ModEntry entry);

    void onModAdded(ModEntry entry);
}
