package com.github.jeuxjeux20.loupsgarous.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.condition.PickConditions;

public abstract class PlayerPick extends Pick<LGPlayer> {
    protected PlayerPick(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected final PickConditions<LGPlayer> criticalConditions() {
        return CriticalPickableConditions.player(orchestrator);
    }
}
