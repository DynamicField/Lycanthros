package com.github.df.loupsgarous.interaction;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.interaction.condition.PickConditions;

public abstract class CouplePick extends Pick<Couple> {
    protected CouplePick(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected final PickConditions<Couple> criticalConditions() {
        return CriticalPickableConditions.couple(orchestrator);
    }
}
