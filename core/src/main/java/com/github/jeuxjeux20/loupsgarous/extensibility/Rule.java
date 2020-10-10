package com.github.jeuxjeux20.loupsgarous.extensibility;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorDependent;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Rule implements OrchestratorDependent {
    protected final LGGameOrchestrator orchestrator;

    private GameBox gameBox;
    private Mod mod;
    private State state = State.DETACHED;

    public Rule(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public final LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    public abstract List<Extension<?>> getExtensions();

    public final boolean isEnabled() {
        return state == State.ACTIVATED;
    }

    public void attach() {
        getGameBox().attachRule(null, this);
    }

    public void detach() {
        getGameBox().detachRule(this);
    }

    public void enable() {
        getGameBox().activateRule(this);
        getGameBox().completeOperation();
    }

    public void disable() {
        getGameBox().deactivateRule(this);
        getGameBox().completeOperation();
    }

    public void refresh() {
        getGameBox().refreshRule(this);
        getGameBox().completeOperation();
    }

    protected void activate() {
    }

    protected void deactivate() {
    }

    public final State getState() {
        return state;
    }

    final void setState(State state) {
        this.state = state;
    }

    final GameBox getGameBox() {
        return gameBox == null ?
                Preconditions.checkNotNull(orchestrator.getGameBox(),
                        "Failed to retrieve the GameBox in this rule.") :
                gameBox;
    }

    final void setGameBox(GameBox gameBox) {
        this.gameBox = gameBox;
    }

    public final @Nullable Mod getMod() {
        return mod;
    }

    final void setMod(Mod mod) {
        this.mod = mod;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("state", state)
                .toString();
    }

    public enum State {
        DETACHED,
        ATTACHED,
        ACTIVATED
    }
}
