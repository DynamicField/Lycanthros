package com.github.jeuxjeux20.loupsgarous.event.registry;

import com.github.jeuxjeux20.loupsgarous.extensibility.registry.Registry;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RegistryChangeEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final Registry<?> registry;

    public RegistryChangeEvent(Registry<?> registry) {
        this.registry = registry;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Registry<?> getRegistry() {
        return registry;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}