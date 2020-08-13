package com.github.jeuxjeux20.loupsgarous.phases.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;

public interface PhaseOverride {
    boolean shouldOverride(LGGameOrchestrator orchestrator);

    void onceComplete(LGGameOrchestrator orchestrator);

    Class<? extends RunnableLGPhase> getPhaseClass();

    default RunnableLGPhase.Factory<?> getPhaseFactory() {
        return orchestrator -> orchestrator.create(getPhaseClass());
    }
}
