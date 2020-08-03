package com.github.jeuxjeux20.loupsgarous.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.configurate.objectmapping.Setting;
import org.spongepowered.configurate.serialize.ConfigSerializable;

import java.nio.file.Path;

@ConfigSerializable
public final class RootConfiguration {
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
        protected ObjectMapper<RootConfiguration> getMapper() throws ObjectMappingException {
            return ObjectMapper.forClass(RootConfiguration.class);
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
