package com.github.df.loupsgarous.config;

import org.bukkit.plugin.Plugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.logging.Level;

public abstract class BukkitConfigurationFile<T> implements ConfigurationFile<T> {
    private final Plugin plugin;

    private CommentedConfigurationNode rootNode = CommentedConfigurationNode.root();
    private final YamlConfigurationLoader loader;

    public BukkitConfigurationFile(Plugin plugin) {
        this.plugin = plugin;

        YamlConfigurationLoader.Builder builder = YamlConfigurationLoader.builder()
                .path(getPath(plugin.getDataFolder().toPath()))
                .nodeStyle(NodeStyle.FLOW);
        configureLoader(builder);
        this.loader = builder.build();

        reload();
    }

    protected final CommentedConfigurationNode getRootNode() {
        return rootNode;
    }

    protected final YamlConfigurationLoader getLoader() {
        return loader;
    }

    protected final Plugin getPlugin() {
        return plugin;
    }

    @Override
    public final void reload() {
        try {
            unsafeReload();

            if (rootNode.empty()) {
                save();
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Couldn't load the configuration, now using the default one.", e);
        }
    }

    protected void unsafeReload() throws Exception {
        rootNode = loader.load();
    }

    @Override
    public final void save() {
        try {
            unsafeSave();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save the configuration.", e);
        }
    }

    protected void unsafeSave() throws Exception {
        loader.save(rootNode);
    }

    protected abstract Path getPath(Path pluginDataFolder);

    protected void configureLoader(YamlConfigurationLoader.Builder builder) {
    }
}
