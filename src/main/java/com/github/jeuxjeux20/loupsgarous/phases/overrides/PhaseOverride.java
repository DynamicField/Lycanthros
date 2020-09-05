package com.github.jeuxjeux20.loupsgarous.phases.overrides;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.phases.RunnableLGPhase;

public interface PhaseOverride {
    boolean shouldOverride(LGGameOrchestrator orchestrator);

    Class<? extends RunnableLGPhase> getPhaseClass();

    RunnableLGPhase.Factory<?> getPhaseFactory();
}
