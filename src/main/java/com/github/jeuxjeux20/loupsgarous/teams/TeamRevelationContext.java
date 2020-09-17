package com.github.jeuxjeux20.loupsgarous.teams;

import com.github.jeuxjeux20.loupsgarous.RevelationContext;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

public class TeamRevelationContext extends RevelationContext {
    private final LGTeam team;

    protected TeamRevelationContext(LGGameOrchestrator orchestrator,
                                    LGPlayer viewer, LGPlayer holder, LGTeam team) {
        super(orchestrator, viewer, holder);
        this.team = team;
    }

    public LGTeam getTeam() {
        return team;
    }
}
