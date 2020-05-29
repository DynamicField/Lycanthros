package com.github.jeuxjeux20.loupsgarous.config;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.inject.Inject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class PluginLGConfiguration implements LGConfiguration {
    public static final String CONFIG_PATH = "configuration";

    private final LoupsGarous plugin;

    @Inject
    public PluginLGConfiguration(LoupsGarous plugin) {
        this.plugin = plugin;
    }

    @Override
    public Optional<String> getDefaultWorld() {
        return Optional.ofNullable(getRootConfig().getDefaultWorld());
    }

    @Override
    public void setDefaultWorld(@Nullable String defaultWorld) {
        updateRootConfig(c -> c.withDefaultWorld(defaultWorld));
    }

    @Override
    public WorldPoolConfiguration getWorldPool() {
        return getRootConfig().getWorldPool();
    }

    @Override
    public void setWorldPool(WorldPoolConfiguration worldPool) {
        updateRootConfig(c -> c.withWorldPool(worldPool));
    }

    // Root stuff

    private void updateRootConfig(Function<RootConfiguration, RootConfiguration> updater) {
        updateRootConfig(updater.apply(getRootConfig()));
    }

    private void updateRootConfig(RootConfiguration configuration) {
        plugin.getConfig().set(CONFIG_PATH, configuration);
        plugin.saveConfig();
    }

    private RootConfiguration getRootConfig() {
        Object configuration = plugin.getConfig().get(CONFIG_PATH);
        if (configuration instanceof RootConfiguration) {
            return ((RootConfiguration) configuration);
        }
        else {
            RootConfiguration rootConfiguration = new RootConfiguration();
            updateRootConfig(rootConfiguration);
            return rootConfiguration;
        }
    }
}
