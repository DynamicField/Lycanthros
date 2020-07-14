package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.PickConditions;

public abstract class AbstractPlayerPick extends AbstractPick<LGPlayer> {
    protected AbstractPlayerPick(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected final PickConditions<LGPlayer> criticalConditions() {
        return CriticalPickableConditions.player(orchestrator);
    }
}
