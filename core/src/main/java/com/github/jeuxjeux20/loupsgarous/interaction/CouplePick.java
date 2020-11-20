package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;

public abstract class CouplePick extends Pick<Couple> {
    protected CouplePick(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected final PickConditions<Couple> criticalConditions() {
        return CriticalPickableConditions.couple(orchestrator);
    }
}
