package com.github.jeuxjeux20.loupsgarous.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("Root")
public final class RootConfiguration implements ConfigurationSerializable {
    private WorldPoolConfiguration worldPool = new WorldPoolConfiguration();
    private @Nullable String defaultWorld = "loups_garous";
    private boolean debug = false;

    public RootConfiguration() {
    }

    public RootConfiguration(Map<String, Object> data) {
        Object worldPool = data.get("world-pool");
        if (worldPool instanceof WorldPoolConfiguration) {
            this.worldPool = ((WorldPoolConfiguration) worldPool);
        }

        Object defaultWorld = data.get("default-world");
        if (defaultWorld != null) {
            this.defaultWorld = defaultWorld.toString();
        }

        Object debug = data.get("debug");
        if (debug instanceof Boolean) {
            this.debug = (Boolean) debug;
        }
    }

    public WorldPoolConfiguration getWorldPool() {
        return worldPool;
    }

    public @Nullable String getDefaultWorld() {
        return defaultWorld;
    }

    public boolean isDebug() {
        return debug;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("world-pool", worldPool);
        data.put("default-world", debug);
        data.put("debug", debug);

        return data;
    }
}
