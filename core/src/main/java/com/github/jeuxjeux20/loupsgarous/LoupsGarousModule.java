package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginModule;
import com.github.jeuxjeux20.loupsgarous.atmosphere.LGAtmosphereModule;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatModule;
import com.github.jeuxjeux20.loupsgarous.commands.LGCommandsModule;
import com.github.jeuxjeux20.loupsgarous.commands.RootCommandsModule;
import com.github.jeuxjeux20.loupsgarous.game.LGGameModule;
import com.github.jeuxjeux20.loupsgarous.listeners.LGListenersModule;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhasesModule;
import com.github.jeuxjeux20.loupsgarous.signs.LGSignsModule;
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

        install(new LGListenersModule());
        install(new LGPhasesModule());
        install(new LGCommandsModule());
        install(new LGChatModule());
        install(new LGSignsModule());
        install(new LGAtmosphereModule());
        install(new LGGameModule());

        bind(Logger.class).annotatedWith(Plugin.class).toInstance(plugin.getLogger());

        bind(Random.class).toInstance(new Random());
    }

    @Override
    protected void configurePlugin() {
        super.configurePlugin();
        bind(HelperPlugin.class).toInstance(plugin);
    }
}
