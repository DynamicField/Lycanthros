package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class AbstractInteractable implements Interactable {
    protected final LGGameOrchestrator orchestrator;

    private boolean isClosed = false;
    private boolean isClosing = false;

    protected AbstractInteractable(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public final void close() throws Exception {
        if (isClosed || isClosing) {
            return;
        }
        isClosing = true;

        closeResources();

        isClosed = true;
    }

    @OverridingMethodsMustInvokeSuper
    protected void closeResources() throws Exception {

    }

    @Override
    public final boolean isClosed() {
        return isClosed;
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    protected void throwIfClosed() {
        if (isClosed()) {
            throw new IllegalStateException("This instance is closed.");
        }
    }
}
