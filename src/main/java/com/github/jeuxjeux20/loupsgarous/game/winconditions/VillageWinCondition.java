package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.endings.VillageWonEnding;

public final class VillageWinCondition extends OnlyAliveTeamWinCondition {
    public VillageWinCondition() {
        super(LGTeams.VILLAGEOIS, VillageWonEnding::new);
    }
}
