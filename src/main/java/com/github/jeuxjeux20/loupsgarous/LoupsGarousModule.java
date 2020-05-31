package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginModule;
import com.github.jeuxjeux20.loupsgarous.commands.RootCommandsModule;
import com.github.jeuxjeux20.loupsgarous.config.ConfigurationModule;
import com.github.jeuxjeux20.loupsgarous.game.LGGameModule;
import com.github.jeuxjeux20.loupsgarous.signs.SignsModule;
import com.onarandombox.MultiverseCore.MultiverseCore;
import me.lucko.helper.plugin.HelperPlugin;

import java.util.Random;
import java.util.logging.Logger;

public final class LoupsGarousModule extends PluginModule {
    private final LoupsGarous plugin;

    public LoupsGarousModule(LoupsGarous plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    protected void configureBindings() {
        install(new RootCommandsModule());
        install(new ConfigurationModule());
        install(new LGGameModule());
        install(new SignsModule());

        bind(MultiverseCore.class).toInstance(getMultiverseCore());
        bind(Logger.class).annotatedWith(Plugin.class).toInstance(plugin.getLogger());

        bind(Random.class).toInstance(new Random());
    }

    @Override
    protected void configurePlugin() {
        super.configurePlugin();
        bind(HelperPlugin.class).toInstance(plugin);
    }

    private MultiverseCore getMultiverseCore() {
        return (MultiverseCore) plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
    }
}
