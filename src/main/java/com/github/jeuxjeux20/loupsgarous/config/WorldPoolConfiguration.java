package com.github.jeuxjeux20.loupsgarous.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

@SerializableAs("WorldPool")
public final class WorldPoolConfiguration implements ConfigurationSerializable {
    private int minWorlds = 8;
    private @Nullable Integer maxWorlds = null;

    public WorldPoolConfiguration() {
    }

    public WorldPoolConfiguration(Map<String, Object> data) {
        this.minWorlds = NumberConversions.toInt(data.getOrDefault("min-worlds", minWorlds));

        this.maxWorlds = NumberConversions.toInt(data.getOrDefault("max-worlds", maxWorlds));
        if (maxWorlds == 0) maxWorlds = null;
    }

    public int getMinWorlds() {
        return minWorlds;
    }

    public OptionalInt getMaxWorlds() {
        return maxWorlds == null ? OptionalInt.empty() : OptionalInt.of(maxWorlds);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("min-worlds", minWorlds);
        data.put("max-worlds", maxWorlds);

        return data;
    }
}
