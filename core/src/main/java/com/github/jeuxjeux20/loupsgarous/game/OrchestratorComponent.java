package com.github.jeuxjeux20.loupsgarous.game;

import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;

import javax.annotation.Nonnull;

public abstract class OrchestratorComponent
        implements TerminableConsumer, Terminable, OrchestratorDependent {
    protected final LGGameOrchestrator orchestrator;

    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    public OrchestratorComponent(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
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
