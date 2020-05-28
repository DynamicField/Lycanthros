package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginModule;
import com.github.jeuxjeux20.loupsgarous.commands.LGRootCommandsModule;
import com.github.jeuxjeux20.loupsgarous.config.LGConfigurationModule;
import com.github.jeuxjeux20.loupsgarous.game.LoupsGarousGameModule;
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
        install(new LGRootCommandsModule());
        install(new LGConfigurationModule());
        install(new LoupsGarousGameModule());

        bind(MultiverseCore.class).toInstance(getMultiverseCore());

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
