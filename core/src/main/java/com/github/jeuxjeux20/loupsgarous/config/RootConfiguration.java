package com.github.jeuxjeux20.loupsgarous.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;

import java.nio.file.Path;

@ConfigSerializable
public final class RootConfiguration {
    private WorldPoolConfiguration worldPool = new WorldPoolConfiguration();

    private @Nullable String defaultWorld = "loups_garous";

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

    // File stuff

    public interface File extends ConfigurationFile<RootConfiguration> {
    }

    public static class BukkitFile
            extends MappedBukkitConfigurationFile<RootConfiguration>
            implements RootConfiguration.File {
        public BukkitFile(Plugin plugin) {
            super(plugin);
        }

        @Override
        protected ObjectMapper<RootConfiguration> getMapper() throws SerializationException {
            return ObjectMapper.factory().get(RootConfiguration.class);
        }

        @Override
        protected RootConfiguration getDefaultValue() {
            return new RootConfiguration();
        }

        @Override
        protected Path getPath(Path pluginDataFolder) {
            return pluginDataFolder.resolve("config.yml");
        }
    }
}
