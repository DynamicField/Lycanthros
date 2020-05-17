package com.github.df.loupsgarous.winconditions;

import com.github.df.loupsgarous.endings.VillageWonEnding;
import com.github.df.loupsgarous.teams.LGTeams;

public final class VillageWinCondition extends OnlyAliveTeamWinCondition {
    public VillageWinCondition() {
        super(LGTeams.VILLAGEOIS, VillageWonEnding::new);
    }
}
