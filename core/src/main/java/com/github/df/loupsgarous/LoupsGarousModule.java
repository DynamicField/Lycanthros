package com.github.df.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginModule;
import com.github.df.loupsgarous.atmosphere.LGAtmosphereModule;
import com.github.df.loupsgarous.chat.LGChatModule;
import com.github.df.loupsgarous.commands.LGCommandsModule;
import com.github.df.loupsgarous.commands.RootCommandsModule;
import com.github.df.loupsgarous.game.LGGameModule;
import com.github.df.loupsgarous.listeners.LGListenersModule;
import com.github.df.loupsgarous.signs.LGSignsModule;
import me.lucko.helper.plugin.HelperPlugin;

import java.util.Random;

final class LoupsGarousModule extends PluginModule {
    private final LoupsGarous plugin;

    public LoupsGarousModule(LoupsGarous plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    protected void configureBindings() {
        install(new RootCommandsModule());

        install(new LGListenersModule());
        install(new LGCommandsModule());
        install(new LGChatModule());
        install(new LGSignsModule());
        install(new LGAtmosphereModule());
        install(new LGGameModule());

        bind(Random.class).toInstance(new Random());
    }

    @Override
    protected void configurePlugin() {
        super.configurePlugin();
        bind(HelperPlugin.class).toInstance(plugin);
    }
}
