package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.base.MoreObjects;
import org.jetbrains.annotations.Nullable;

public abstract class StateTransition {
    private State state = State.READY;
    private boolean enforced;
    private @Nullable Throwable error;

    StateTransition() {
    }

    public State getState() {
        return state;
    }

    void setState(State state) {
        this.state = state;
    }

    public @Nullable Throwable getError() {
        return error;
    }

    void setError(@Nullable Throwable error) {
        this.error = error;
    }

    public boolean isEnforced() {
        return enforced;
    }

    public void setEnforced(boolean enforced) {
        this.enforced = enforced;
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this)
                .add("state", state)
                .add("error", error);

        addToString(helper);
        return helper.toString();
    }

    protected void addToString(MoreObjects.ToStringHelper helper) {
    }

    public enum State {
        READY,
        PENDING,
        RUNNING,
        // Terminations
        COMPLETED,
        ERROR,
        CANCELLED
    }
}
