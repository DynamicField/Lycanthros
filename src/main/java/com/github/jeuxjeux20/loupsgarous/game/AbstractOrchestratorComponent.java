package com.github.jeuxjeux20.loupsgarous.game;

import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;

import javax.annotation.Nonnull;

public abstract class AbstractOrchestratorComponent implements OrchestratorComponent, TerminableConsumer {
    protected final LGGameOrchestrator orchestrator;

    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    public AbstractOrchestratorComponent(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @Override
    public final void close() throws Exception {
        terminableRegistry.close();
    }
}
