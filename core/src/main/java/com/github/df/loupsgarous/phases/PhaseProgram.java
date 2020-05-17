package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.game.LGGameOrchestrator;

public abstract class PhaseProgram {
    protected final LGGameOrchestrator orchestrator;
    private boolean isRunning;

    protected PhaseProgram(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public boolean isRunning() {
        return isRunning;
    }

    void setRunning(boolean running) {
        if (isRunning != running) {
            isRunning = running;

            if (isRunning) {
                startProgram();
            } else {
                stopProgram();
            }
        }
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
        return ((ActualPhasesOrchestrator) orchestrator.phases()).getPhaseRunner();
    }
}
