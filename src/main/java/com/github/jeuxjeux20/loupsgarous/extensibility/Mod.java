package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

public abstract class Mod {
    protected void configureDefaults(ConfigurationNode configuration) {
    }

    public final ConfigurationNode getDefaultConfiguration() {
        BasicConfigurationNode configuration = BasicConfigurationNode.root();

        configureDefaults(configuration);

        return configuration;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public abstract List<Rule> createRules(LGGameOrchestrator orchestrator, ConfigurationNode configuration);
}
