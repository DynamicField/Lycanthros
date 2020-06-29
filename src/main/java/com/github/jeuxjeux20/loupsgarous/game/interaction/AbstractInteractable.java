package com.github.jeuxjeux20.loupsgarous.game.interaction;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class AbstractInteractable implements Interactable {
    private boolean isClosed = false;

    @Override
    public final void close() throws Exception {
        if (isClosed) {
            return;
        }

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
    public String toString() {
        return this.getClass().getSimpleName();
    }

    protected void throwIfClosed() {
        if (isClosed()) {
            throw new IllegalStateException("This instance is closed.");
        }
    }
}
