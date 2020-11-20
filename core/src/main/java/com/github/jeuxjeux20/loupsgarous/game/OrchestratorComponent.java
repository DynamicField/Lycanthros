package com.github.jeuxjeux20.loupsgarous.game;

import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeTerminable;

import javax.annotation.Nonnull;

public abstract class OrchestratorComponent
        implements TerminableConsumer, Terminable, OrchestratorAware {
    protected final LGGameOrchestrator orchestrator;

    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();
    private boolean closed = false;

    public OrchestratorComponent(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
        terminableRegistry.bind(this::cleanup);
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    protected void onStart() {}
    protected void onStop() {}

    protected void cleanup() {}

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @Override
    public final void close() throws Exception {
        terminableRegistry.close();
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
