package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.endings.VillageWonEnding;

public final class VillageWinCondition extends OnlyAliveTeamWinCondition {
    public VillageWinCondition() {
        super(LGTeams.LOUPS_GAROUS, VillageWonEnding::new);
    }
}
