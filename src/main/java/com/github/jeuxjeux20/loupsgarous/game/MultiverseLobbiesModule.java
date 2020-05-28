package com.github.jeuxjeux20.loupsgarous.game;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public final class MultiverseLobbiesModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder()
                .implement(LobbyTeleporter.class, MultiverseLobbyTeleporter.class)
                .build(LobbyTeleporter.Factory.class));
    }
}
