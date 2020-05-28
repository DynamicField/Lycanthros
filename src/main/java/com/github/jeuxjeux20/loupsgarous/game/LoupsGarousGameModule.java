package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCardsModule;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCardsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.MinecraftLGCardsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatModule;
import com.github.jeuxjeux20.loupsgarous.game.commands.LGCommandsModule;
import com.github.jeuxjeux20.loupsgarous.game.listeners.LGListenersModule;
import com.github.jeuxjeux20.loupsgarous.game.scoreboard.LGScoreboardModule;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStagesModule;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LoupsGarousGameModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new LGCardsModule());
        install(new LGListenersModule());
        install(new LGStagesModule());
        install(new LGCommandsModule());
        install(new LGChatModule());
        install(new LGScoreboardModule());

        bind(LGGameManager.class).to(MinecraftLGGameManager.class);

        install(new FactoryModuleBuilder()
                .implement(LGGameOrchestrator.class, MinecraftLGGameOrchestrator.class)
                .build(LGGameOrchestrator.Factory.class));

        install(new FactoryModuleBuilder()
                .implement(LGCardsOrchestrator.class, MinecraftLGCardsOrchestrator.class)
                .build(LGCardsOrchestrator.Factory.class));

        install(new FactoryModuleBuilder()
                .implement(LGGameLobby.class, MinecraftLGGameLobby.class)
                .build(LGGameLobby.Factory.class));

        bind(LGActionBarManager.class).to(MinecraftLGActionBarManager.class);
    }
}
