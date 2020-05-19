package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginDependencies;
import com.github.jeuxjeux20.guicybukkit.command.CommandConfigurator;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.event.Listener;

import java.util.Set;

public final class LoupsGarous extends ExtendedJavaPlugin {
    @Override
    public void enable() {
        getConfig().options().copyDefaults(true);

        Injector injector = Guice.createInjector(new LoupsGarousModule(this));

        LGPluginDependencies pluginDependencies = injector.getInstance(LGPluginDependencies.class);
        pluginDependencies.registerAll(this);
        pluginDependencies.worldCleaner.clean();
    }

    @Override
    public void disable() {
        saveConfig();
    }

    private static final class LGPluginDependencies extends PluginDependencies {
        public final WorldCleaner worldCleaner;

        @Inject
        public LGPluginDependencies(Set<Listener> listeners, Set<CommandConfigurator> commands, WorldCleaner worldCleaner) {
            super(listeners, commands);
            this.worldCleaner = worldCleaner;
        }
    }
}
