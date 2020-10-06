package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public abstract class PhaseProgram {
    private boolean isRunning;
    protected final LGGameOrchestrator orchestrator;

    protected PhaseProgram(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public boolean isRunning() {
        return isRunning;
    }

    void setRunning(boolean running) {
        isRunning = running;
    }

    public final void start() {
        orchestrator.phases().startProgram(this);
    }

    public final void stop() {
        orchestrator.phases().stopProgram(this);
    }

    protected abstract void startProgram();
    protected abstract void stopProgram();

    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }

    protected PhaseRunner getPhaseRunner() {
        return orchestrator.phases().getPhaseRunner();
    }
}
