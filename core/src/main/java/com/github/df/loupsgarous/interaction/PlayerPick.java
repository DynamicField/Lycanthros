package com.github.df.loupsgarous.interaction;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.condition.PickConditions;

public abstract class PlayerPick extends Pick<LGPlayer> {
    protected PlayerPick(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected final PickConditions<LGPlayer> criticalConditions() {
        return CriticalPickableConditions.player(orchestrator);
    }
}
