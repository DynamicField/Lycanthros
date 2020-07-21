package com.github.jeuxjeux20.loupsgarous.game;

import com.google.inject.AbstractModule;

public final class OrchestratorScopeModule extends AbstractModule {
    private final OrchestratorScope orchestratorScope = new OrchestratorScope();

    @Override
    protected void configure() {
        bindScope(OrchestratorScoped.class, orchestratorScope);

        bind(OrchestratorScope.class).toInstance(orchestratorScope);

        bind(LGGameOrchestrator.class)
                .toProvider(OrchestratorScope.seededKeyProvider())
                .in(OrchestratorScoped.class);

        bind(InternalLGGameOrchestrator.class)
                .toProvider(OrchestratorScope.seededKeyProvider())
                .in(OrchestratorScoped.class);
    }
}
