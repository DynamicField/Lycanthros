package com.github.df.loupsgarous.config;

import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.ConfigurationNode;

public abstract class NodeBukkitConfigurationFile extends BukkitConfigurationFile<ConfigurationNode> {
    public NodeBukkitConfigurationFile(Plugin plugin) {
        super(plugin);
    }

    @Override
    public ConfigurationNode get() {
        return getRootNode();
    }
}
