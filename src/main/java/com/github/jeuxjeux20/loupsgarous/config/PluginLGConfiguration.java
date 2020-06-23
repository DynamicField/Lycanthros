package com.github.jeuxjeux20.loupsgarous.config;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.inject.Inject;

public class PluginLGConfiguration implements LGConfiguration {
    public static final String CONFIG_PATH = "configuration";

    private final LoupsGarous plugin;

    @Inject
    public PluginLGConfiguration(LoupsGarous plugin) {
        this.plugin = plugin;

        reload();
        getRootConfig();
    }

    @Override
    public RootConfiguration get() {
        return getRootConfig();
    }

    @Override
    public void reload() {
        plugin.reloadConfig();
    }

    @Override
    public void save() {
        plugin.saveConfig();
    }

    private void updateRootConfig(RootConfiguration configuration) {
        plugin.getConfig().set(CONFIG_PATH, configuration);
        plugin.saveConfig();
    }

    private RootConfiguration getRootConfig() {
        Object configuration = plugin.getConfig().get(CONFIG_PATH);
        if (configuration instanceof RootConfiguration) {
            return ((RootConfiguration) configuration);
        } else {
            RootConfiguration rootConfiguration = new RootConfiguration();
            updateRootConfig(rootConfiguration);
            return rootConfiguration;
        }
    }
}
