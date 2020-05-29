package com.github.jeuxjeux20.loupsgarous.config;

import com.github.jeuxjeux20.loupsgarous.config.serialization.BukkitDeserialize;
import com.github.jeuxjeux20.loupsgarous.config.serialization.SimpleYamlMapper;
import com.github.jeuxjeux20.loupsgarous.config.serialization.YamlProperty;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SerializableAs("Root")
public final class RootConfiguration implements ConfigurationSerializable {
    @YamlProperty("world-pool")
    private WorldPoolConfiguration worldPool;

    @YamlProperty("default-world")
    private @Nullable String defaultWorld;

    public RootConfiguration(WorldPoolConfiguration worldPool, @Nullable String defaultWorld) {
        this.worldPool = worldPool;
        this.defaultWorld = defaultWorld;

        checkData();
    }

    public RootConfiguration() {
        this(new WorldPoolConfiguration(), "loups_garous");
    }

    @BukkitDeserialize
    public RootConfiguration(Map<String, Object> data) {
        SimpleYamlMapper.deserializeFields(data, this);

        checkData();
    }

    private void checkData() {
        if (worldPool == null) worldPool = new WorldPoolConfiguration();
    }

    public WorldPoolConfiguration getWorldPool() {
        return worldPool;
    }

    public RootConfiguration withWorldPool(WorldPoolConfiguration worldPool) {
        return new RootConfiguration(worldPool, defaultWorld);
    }

    public @Nullable String getDefaultWorld() {
        return defaultWorld;
    }

    public RootConfiguration withDefaultWorld(String defaultWorld) {
        return new RootConfiguration(worldPool, defaultWorld);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return SimpleYamlMapper.serialize(this);
    }
}
