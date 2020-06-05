package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.throwingproviders.ThrowingProviderBinder;

public final class MultiverseLobbiesModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MinecraftMultiverseWorldPool.class).asEagerSingleton();

        bind(MultiverseWorldPool.class).to(MinecraftMultiverseWorldPool.class);
        bind(LobbyPresenceChecker.class).to(MinecraftMultiverseWorldPool.class);

        ThrowingProviderBinder.create(binder())
                .bind(MultiverseWorldProvider.class, TerminableMultiverseWorld.class)
                .to(MultiverseWorldPool.class);

        install(new FactoryModuleBuilder()
                .implement(LobbyTeleporter.class, MultiverseLobbyTeleporter.class)
                .build(LobbyTeleporter.Factory.class));

        bind(SpawnTeleporter.class).to(MultiverseSpawnTeleporter.class);
    }
}
