package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;

public abstract class AbstractCouplePickable extends AbstractPickable<Couple> {
    protected AbstractCouplePickable(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected final PickConditions<Couple> criticalConditions() {
        return CriticalPickableConditions.couple(orchestrator);
    }
}
