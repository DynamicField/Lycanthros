package com.github.jeuxjeux20.loupsgarous;

import com.github.jeuxjeux20.guicybukkit.PluginModule;
import com.github.jeuxjeux20.loupsgarous.actionbar.LGActionBarModule;
import com.github.jeuxjeux20.loupsgarous.atmosphere.LGAtmosphereModule;
import com.github.jeuxjeux20.loupsgarous.bossbar.LGBossBarModule;
import com.github.jeuxjeux20.loupsgarous.cards.LGCardsModule;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatModule;
import com.github.jeuxjeux20.loupsgarous.commands.LGCommandsModule;
import com.github.jeuxjeux20.loupsgarous.commands.RootCommandsModule;
import com.github.jeuxjeux20.loupsgarous.game.LGGameModule;
import com.github.jeuxjeux20.loupsgarous.interaction.LGInteractionModule;
import com.github.jeuxjeux20.loupsgarous.inventory.LGInventoryModule;
import com.github.jeuxjeux20.loupsgarous.kill.LGKillModule;
import com.github.jeuxjeux20.loupsgarous.listeners.LGListenersModule;
import com.github.jeuxjeux20.loupsgarous.lobby.LGLobbyModule;
import com.github.jeuxjeux20.loupsgarous.scoreboard.LGScoreboardModule;
import com.github.jeuxjeux20.loupsgarous.signs.LGSignsModule;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhasesModule;
import com.github.jeuxjeux20.loupsgarous.tags.LGTagsModule;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeamsModule;
import com.github.jeuxjeux20.loupsgarous.winconditions.LGWinConditionsModule;
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

        install(new LGCardsModule());
        install(new LGListenersModule());
        install(new LGPhasesModule());
        install(new LGCommandsModule());
        install(new LGChatModule());
        install(new LGScoreboardModule());
        install(new LGLobbyModule());
        install(new LGWinConditionsModule());
        install(new LGInteractionModule());
        install(new LGTeamsModule());
        install(new LGSignsModule());
        install(new LGInventoryModule());
        install(new LGKillModule());
        install(new LGActionBarModule());
        install(new LGAtmosphereModule());
        install(new LGBossBarModule());
        install(new LGTagsModule());
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
