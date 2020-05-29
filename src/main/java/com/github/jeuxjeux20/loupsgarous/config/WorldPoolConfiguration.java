package com.github.jeuxjeux20.loupsgarous.config;

import com.github.jeuxjeux20.loupsgarous.config.serialization.BukkitDeserialize;
import com.github.jeuxjeux20.loupsgarous.config.serialization.SimpleYamlMapper;
import com.github.jeuxjeux20.loupsgarous.config.serialization.YamlProperty;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SerializableAs("WorldPool")
public final class WorldPoolConfiguration implements ConfigurationSerializable {
    @YamlProperty("min-worlds")
    private int minWorlds;

    @YamlProperty("max-worlds")
    private @Nullable Integer maxWorlds;

    public WorldPoolConfiguration(int minWorlds, @Nullable Integer maxWorlds) {
        this.minWorlds = minWorlds;
        this.maxWorlds = maxWorlds;

        checkData();
    }

    public WorldPoolConfiguration() {
        this(8, null);
    }

    @BukkitDeserialize
    public WorldPoolConfiguration(Map<String, Object> data) {
        SimpleYamlMapper.deserializeFields(data, this);

        checkData();
    }

    private void checkData() {
        if (minWorlds <= 0) minWorlds = 1;
        if (maxWorlds != null && maxWorlds < minWorlds) maxWorlds = minWorlds;
    }

    public int getMinWorlds() {
        return minWorlds;
    }

    public WorldPoolConfiguration withMinWorlds(int minWorlds) {
        return new WorldPoolConfiguration(minWorlds, maxWorlds);
    }

    public @Nullable Integer getMaxWorlds() {
        return maxWorlds;
    }

    public WorldPoolConfiguration withMaxWorlds(@Nullable Integer maxWorlds) {
        return new WorldPoolConfiguration(minWorlds, maxWorlds);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SimpleYamlMapper.serialize(this);
    }
}
