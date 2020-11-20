package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.Registration;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorAware;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class Interactable implements OrchestratorAware {
    protected final LGGameOrchestrator orchestrator;

    private @Nullable String key;

    protected Interactable(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public @Nullable String getKey() {
        return key;
    }

    void setKey(@Nullable String key) {
        if (!Objects.equals(this.key, key)) {
            this.key = key;

            if (key != null) {
                onRegister();
            } else {
                onUnregister();
            }
        }
    }

    public boolean isRegistered() {
        return key != null;
    }

    public final Registration register(String key) {
        return orchestrator.interactables().register(key, this);
    }

    protected void onRegister() {}

    public final void unregister() {
        orchestrator.interactables().unregister(this);
    }

    protected void onUnregister() {}
}
