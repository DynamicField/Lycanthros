package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginDependencies;
import com.github.jeuxjeux20.guicybukkit.command.CommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandNotFoundException;
import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.config.RootConfiguration;
import com.github.jeuxjeux20.loupsgarous.extensibility.ModRegistry;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.lobby.MultiverseLobbiesModule;
import com.google.common.collect.ImmutableList;
import com.google.inject.*;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * The main loups-garous plugin! Woof!
 */
public final class LoupsGarous extends ExtendedJavaPlugin implements LoupsGarousRoot {
    private LGPluginDependencies pluginDependencies;

    @Override
    public void enable() {
        Injector injector = Guice.createInjector(getModules());

        pluginDependencies = injector.getInstance(LGPluginDependencies.class);
        pluginDependencies.registerAll(this);
    }

    private ImmutableList<Module> getModules() {
        RootConfiguration.File configuration = new RootConfiguration.BukkitFile(this);

        ImmutableList.Builder<Module> modulesBuilder = ImmutableList.<Module>builder()
                .add(new LoupsGarousModule(this))
                .add(new MultiverseLobbiesModule())
                .add(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(RootConfiguration.File.class).toInstance(configuration);
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

    @Override
    public LGGameManager getGameManager() {
        return pluginDependencies.gameManager;
    }

    @Override
    public ModRegistry getModRegistry() {
        return pluginDependencies.modRegistry;
    }

    @Override
    public RootConfiguration.File getRootConfig() {
        return pluginDependencies.rootConfig;
    }

    private static final class LGPluginDependencies extends PluginDependencies {
        @Inject
        LGGameManager gameManager;

        @Inject
        ModRegistry modRegistry;

        @Inject
        RootConfiguration.File rootConfig;

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
