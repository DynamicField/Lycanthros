package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.endings.VillageWonEnding;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;

public final class VillageWinCondition extends OnlyAliveTeamWinCondition {
    public VillageWinCondition() {
        super(LGTeams.VILLAGEOIS, VillageWonEnding::new);
    }
}
