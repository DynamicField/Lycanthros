package com.github.jeuxjeux20.loupsgarous.winconditions;

import com.github.jeuxjeux20.loupsgarous.endings.VillageWonEnding;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;

public final class VillageWinCondition extends OnlyAliveTeamWinCondition {
    public VillageWinCondition() {
        super(LGTeams.VILLAGEOIS, VillageWonEnding::new);
    }
}
