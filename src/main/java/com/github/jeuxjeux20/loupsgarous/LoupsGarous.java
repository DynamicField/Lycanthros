package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginDependencies;
import com.github.jeuxjeux20.guicybukkit.command.CommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandNotFoundException;
import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.config.*;
import com.github.jeuxjeux20.loupsgarous.lobby.MultiverseLobbiesModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

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
        Injector injector = Guice.createInjector(getModules());

        LGPluginDependencies pluginDependencies = injector.getInstance(LGPluginDependencies.class);
        pluginDependencies.registerAll(this);
    }

    private ImmutableList<Module> getModules() {
        PluginLGConfiguration configuration = new PluginLGConfiguration(this);

        ImmutableList.Builder<Module> modulesBuilder = ImmutableList.<Module>builder()
                .add(new LoupsGarousModule(this))
                .add(new MultiverseLobbiesModule())
                .add(new ConfigurationModule() {
                    @Override
                    protected void bindConfiguration() {
                        bind(LGConfiguration.class).toInstance(configuration);
                    }
                });

        if (configuration.get().isDebug()) {
            modulesBuilder.add(new DebugModule());
        }

        return modulesBuilder.build();
    }

    @Override
    public void disable() {
        // Some stuff. Maybe.
    }

    private static final class LGPluginDependencies extends PluginDependencies {
        @Inject
        public LGPluginDependencies(Set<Listener> listeners, Set<CommandConfigurator> commands) {
            super(listeners, commands);
        }

        @Override
        public void registerCommands(@NotNull CommandConfigurator.CommandFinder commandFinder) {
            for (CommandConfigurator configurator : getCommandsConfigurators()) {
                if (configurator instanceof HelperCommandRegisterer) {
                    ((HelperCommandRegisterer) configurator).register();
                } else {
                    String commandName = configurator.getCommandName();
                    PluginCommand pluginCommand = commandFinder.find(commandName);
                    if (pluginCommand == null) {
                        throw new CommandNotFoundException("Couldn't find the command " + commandName + ".");
                    }
                    configurator.configureCommand(pluginCommand);
                }
            }
        }
    }
}
