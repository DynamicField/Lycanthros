package com.github.jeuxjeux20.loupsgarous.config;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ScopedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.configurate.objectmapping.Setting;
import org.spongepowered.configurate.serialize.ConfigSerializable;

@ConfigSerializable
public final class RootConfiguration {
    static final ObjectMapper<RootConfiguration> MAPPER;

    static {
        try {
            MAPPER = ObjectMapper.forClass(RootConfiguration.class);
        } catch (ObjectMappingException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    static <N extends ScopedConfigurationNode<N>> RootConfiguration loadFrom(N node)
            throws ObjectMappingException {
        return MAPPER.bindToNew().populate(node);
    }

    @Setting(value = "world-pool")
    private WorldPoolConfiguration worldPool = new WorldPoolConfiguration();

    @Setting(value = "default-world")
    private @Nullable String defaultWorld = "loups_garous";

    @Setting
    private boolean debug = false;

    public WorldPoolConfiguration getWorldPool() {
        return worldPool;
    }

    public void setWorldPool(WorldPoolConfiguration worldPool) {
        this.worldPool = worldPool;
    }

    public @Nullable String getDefaultWorld() {
        return defaultWorld;
    }

    public void setDefaultWorld(@Nullable String defaultWorld) {
        this.defaultWorld = defaultWorld;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    <N extends ScopedConfigurationNode<N>> void saveTo(N node) throws ObjectMappingException {
        MAPPER.bind(this).serialize(node);
    }
}
