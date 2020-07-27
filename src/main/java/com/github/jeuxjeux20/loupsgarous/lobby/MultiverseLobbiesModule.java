package com.github.jeuxjeux20.loupsgarous.lobby;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import com.onarandombox.MultiverseCore.MultiverseCore;

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

    @Provides
    MultiverseCore provideMultiverse(LoupsGarous plugin) {
        return (MultiverseCore) plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
    }
}
