package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class LGLobbyModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(LGGameLobby.class, MinecraftLGGameLobby.class)
                .build(LGGameLobby.Factory.class));
    }
}
