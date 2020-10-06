package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhaseDescriptorRegistry;
import com.google.inject.Inject;
import org.jetbrains.annotations.Nullable;

public class LGPhasesOrchestrator extends OrchestratorComponent {
    private final LGPhaseDescriptorRegistry descriptorRegistry;
    private final PhaseRunner phaseRunner;

    private @Nullable PhaseProgram program;

    @Inject
    LGPhasesOrchestrator(LGGameOrchestrator orchestrator,
                         LGPhaseDescriptorRegistry descriptorRegistry) {
        super(orchestrator);
        this.descriptorRegistry = descriptorRegistry;
        this.phaseRunner = new PhaseRunner(orchestrator);
        this.program = new EmptyPhaseProgram(orchestrator);

        bind(() -> stopProgram(program));
    }

    public LGPhase current() {
        PhaseRunner.RunToken currentToken = phaseRunner.getCurrent();
        return currentToken == null ? new LGPhase.Null(orchestrator) : currentToken.getPhase();
    }

    public @Nullable PhaseProgram getProgram() {
        return program;
    }

    void startProgram(PhaseProgram program) {
        if (program == null || this.program == program || program.isRunning()) {
            return;
        }

        stopProgram(this.program);
        this.program = program;
        program.setRunning(true);
        program.startProgram();
    }

    void stopProgram(PhaseProgram program) {
        if (program == null || this.program != program || !program.isRunning()) {
            return;
        }

        this.program = null;
        program.setRunning(false);
        program.stopProgram();
        getPhaseRunner().terminateCurrent();
    }

    PhaseRunner getPhaseRunner() {
        return phaseRunner;
    }

    /**
     * Returns the descriptor registry.
     *
     * @return the descriptor registry
     */
    public LGPhaseDescriptorRegistry descriptors() {
        return descriptorRegistry;
    }

    @Override
    public LGGameOrchestrator getOrchestrator() {
        return orchestrator;
    }
}
