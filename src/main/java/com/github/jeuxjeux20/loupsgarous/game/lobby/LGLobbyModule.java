package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

public final class LGLobbyModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MultiverseWorldPool.class).to(MinecraftMultiverseWorldPool.class).asEagerSingleton();

        ThrowingProviderBinder.create(binder())
                .bind(MultiverseWorldProvider.class, TerminableMultiverseWorld.class)
                .to(MultiverseWorldPool.class);

        install(new FactoryModuleBuilder()
                .implement(LGGameLobby.class, MinecraftLGGameLobby.class)
                .build(LGGameLobby.Factory.class));
    }
}
