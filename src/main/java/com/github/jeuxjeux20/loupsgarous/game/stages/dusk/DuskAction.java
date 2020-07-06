package com.github.jeuxjeux20.loupsgarous.game.stages.dusk;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import com.github.jeuxjeux20.loupsgarous.game.interaction.SelfBoundOrchestratorInteractableRegisterer;
import com.google.inject.Inject;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;

import javax.annotation.Nonnull;

public abstract class DuskAction
        implements Terminable, TerminableConsumer, LGGameOrchestratorDependent, SelfBoundOrchestratorInteractableRegisterer {
    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

    protected final LGGameOrchestrator orchestrator;

    @Inject
    protected DuskAction(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    abstract protected boolean shouldRun();

    abstract protected String getMessage();

    protected void onDuskStart() {
    }

    protected void onDuskEnd() {
    }

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }

    @Override
    public final void close() throws CompositeClosingException {
        terminableRegistry.close();
    }
}
