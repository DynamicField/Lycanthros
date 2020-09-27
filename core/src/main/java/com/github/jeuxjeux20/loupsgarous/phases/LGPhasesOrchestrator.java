package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.phases.descriptor.LGPhaseDescriptorRegistry;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

public class LGPhasesOrchestrator extends OrchestratorComponent {
    private final LGPhaseDescriptorRegistry descriptorRegistry;

    private PhaseCycle cycle;

    @Inject
    LGPhasesOrchestrator(LGGameOrchestrator orchestrator,
                         LGPhaseDescriptorRegistry descriptorRegistry) {
        super(orchestrator);
        this.descriptorRegistry = descriptorRegistry;
        setCycle(new EmptyPhaseCycle(orchestrator));

        bind(() -> cycle.close());
    }

    public LGPhase current() {
        return cycle.current();
    }

    public PhaseCycle getCycle() {
        return cycle;
    }

    public void setCycle(PhaseCycle cycle) {
        Preconditions.checkNotNull(cycle, "cycle is null");

        if (this.cycle != null) {
            this.cycle.stop();
            this.cycle.closeAndReportException();
        }

        this.cycle = cycle;
        this.cycle.start();
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
