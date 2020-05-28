package com.github.jeuxjeux20.loupsgarous.config;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.inject.Inject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PluginLGConfiguration implements LGConfiguration {
    private final LoupsGarous plugin;

    @Inject
    public PluginLGConfiguration(LoupsGarous plugin) {
        this.plugin = plugin;
    }

    @Override
    public Optional<String> getDefaultWorld() {
        return Optional.ofNullable(plugin.getConfig().getString("default-world"));
    }

    @Override
    public void setDefaultWorld(@Nullable String defaultWorld) {
        plugin.getConfig().set("default-world", defaultWorld);
        plugin.saveConfig();
    }
}
