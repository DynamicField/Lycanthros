package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginModule;
import com.github.jeuxjeux20.guicybukkit.command.CommandConfigurator;
import com.github.jeuxjeux20.loupsgarous.commands.*;
import com.github.jeuxjeux20.loupsgarous.config.LGConfiguration;
import com.github.jeuxjeux20.loupsgarous.config.PluginLGConfiguration;
import com.github.jeuxjeux20.loupsgarous.game.LoupsGarousGameModule;
import com.google.inject.multibindings.Multibinder;
import com.onarandombox.MultiverseCore.MultiverseCore;
import me.lucko.helper.plugin.HelperPlugin;

import java.util.Random;

public final class LoupsGarousModule extends PluginModule {
    private final LoupsGarous plugin;

    public LoupsGarousModule(LoupsGarous plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    protected void configureBindings() {
        install(new LoupsGarousGameModule());

        bind(MultiverseCore.class).toInstance(getMultiverseCore());
        bind(PermissionChecker.class).to(SuperPermsPermissionChecker.class);
        bind(LGConfiguration.class).to(PluginLGConfiguration.class);

        bind(Random.class).toInstance(new Random());
    }

    @Override
    protected void configurePlugin() {
        super.configurePlugin();
        bind(HelperPlugin.class).toInstance(plugin);
    }

    @Override
    protected void configureCommands(Multibinder<CommandConfigurator> binder) {
        binder.addBinding().to(LGStartCommand.class);
        binder.addBinding().to(LGListCommand.class);
        binder.addBinding().to(LGConfigCommand.class);
        binder.addBinding().to(LGFinishCommand.class);
        binder.addBinding().to(ColorCommand.class);
        binder.addBinding().to(GuiTestCommand.class);
    }

    private MultiverseCore getMultiverseCore() {
        return (MultiverseCore) plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
    }
}
