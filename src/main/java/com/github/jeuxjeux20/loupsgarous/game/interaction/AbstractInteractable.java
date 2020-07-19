package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public abstract class AbstractInteractable implements Interactable, TerminableConsumer {
    protected final LGGameOrchestrator orchestrator;

    private final CompositeTerminable terminableRegistry = CompositeTerminable.create();
    private final List<TerminationListener<? super Interactable>> terminationListeners =
            new LinkedList<>();

    private boolean isClosed = false;
    private boolean isClosing = false;

    protected AbstractInteractable(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public final void close() throws CompositeClosingException {
        if (isClosed || isClosing) {
            return;
        }
        isClosing = true;

        try {
            terminableRegistry.close();
        } finally {
            isClosing = false;
            isClosed = true;

            callTerminationListeners();
        }
    }

    @Override
    public final boolean isClosed() {
        return isClosed;
    }

    @Override
    public void addTerminationListener(TerminationListener<? super Interactable> listener) {
        terminationListeners.add(listener);
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    @Override
    @Nonnull
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    protected void throwIfClosed() {
        if (isClosed()) {
            throw new IllegalStateException("This instance is closed.");
        }
    }

    private void callTerminationListeners() {
        for (TerminationListener<? super Interactable> listener : terminationListeners) {
            try {
                listener.afterTermination(this);
            } catch (Exception e) {
                orchestrator.logger().log(Level.SEVERE,
                        "Exception thrown in listener " + listener + " " +
                        "while closing " + this + ".", e
                );
            }
        }
    }
}
