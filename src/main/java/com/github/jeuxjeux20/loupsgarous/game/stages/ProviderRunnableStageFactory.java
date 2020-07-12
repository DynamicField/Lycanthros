package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScope;
import com.google.inject.Provider;

class ProviderRunnableStageFactory<T extends RunnableLGStage> implements RunnableLGStage.Factory<T> {
    private final Provider<T> provider;

    public ProviderRunnableStageFactory(Provider<T> provider) {
        this.provider = provider;
    }

    @Override
    public T create(LGGameOrchestrator gameOrchestrator) {
        try (OrchestratorScope.Block block = gameOrchestrator.scope()) {
            return provider.get();
        }
    }
}
