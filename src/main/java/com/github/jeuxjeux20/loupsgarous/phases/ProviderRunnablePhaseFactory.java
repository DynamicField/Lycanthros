package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScope;
import com.google.common.base.MoreObjects;
import com.google.inject.Provider;

class ProviderRunnablePhaseFactory<T extends RunnableLGPhase> implements RunnableLGPhase.Factory<T> {
    private final Provider<T> provider;

    public ProviderRunnablePhaseFactory(Provider<T> provider) {
        this.provider = provider;
    }

    @Override
    public T create(LGGameOrchestrator gameOrchestrator) {
        try (OrchestratorScope.Block block = gameOrchestrator.scope()) {
            return provider.get();
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("provider", provider)
                .toString();
    }
}
