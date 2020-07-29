package com.github.jeuxjeux20.loupsgarous.config;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.inject.Inject;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.util.logging.Level;

public class PluginLGConfiguration implements LGConfiguration {
    private final LoupsGarous plugin;

    private BasicConfigurationNode rootNode;
    private RootConfiguration rootConfig;
    private final YamlConfigurationLoader loader;

    @Inject
    public PluginLGConfiguration(LoupsGarous plugin) {
        this.plugin = plugin;
        this.loader = YamlConfigurationLoader.builder()
                .setPath(plugin.getDataFolder().toPath().resolve("config.yml"))
                .build();

        reload();
    }

    @Override
    public RootConfiguration get() {
        return rootConfig;
    }

    @Override
    public ConfigurationNode getNode() {
        return rootNode;
    }

    @Override
    public void reload() {
        try {
            rootNode = loader.load();
            rootConfig = RootConfiguration.loadFrom(rootNode);
        } catch (ObjectMappingException | IOException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Couldn't load the configuration, now using the default one.", e);
            if (rootNode == null) {
                rootNode = BasicConfigurationNode.root();
            }
            rootConfig = new RootConfiguration();
        }
    }

    @Override
    public void save() {
        try {
            rootConfig.saveTo(rootNode);
            loader.save(rootNode);
        } catch (ObjectMappingException | IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't save the configuration.", e);
        }
    }
}
