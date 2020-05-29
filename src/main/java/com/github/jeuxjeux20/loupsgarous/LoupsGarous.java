package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginDependencies;
import com.github.jeuxjeux20.guicybukkit.command.CommandConfigurator;
import com.github.jeuxjeux20.loupsgarous.config.RootConfiguration;
import com.github.jeuxjeux20.loupsgarous.config.WorldPoolConfiguration;
import com.github.jeuxjeux20.loupsgarous.game.lobby.MultiverseLobbiesModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;

import java.util.Set;

/**
 * The main loups-garous plugin! Woof!
 */
public final class LoupsGarous extends ExtendedJavaPlugin {
    static {
        ConfigurationSerialization.registerClass(WorldPoolConfiguration.class);
        ConfigurationSerialization.registerClass(RootConfiguration.class);
    }

    @Override
    public void enable() {
        getConfig();

        Injector injector = Guice.createInjector(new LoupsGarousModule(this), new MultiverseLobbiesModule());

        LGPluginDependencies pluginDependencies = injector.getInstance(LGPluginDependencies.class);
        pluginDependencies.registerAll(this);
    }

    @Override
    public void disable() {
        // No need to save, it's saving once the config gets changed.
        // saveConfig();
    }

    private static final class LGPluginDependencies extends PluginDependencies {
        @Inject
        public LGPluginDependencies(Set<Listener> listeners, Set<CommandConfigurator> commands) {
            super(listeners, commands);
        }
    }
}
