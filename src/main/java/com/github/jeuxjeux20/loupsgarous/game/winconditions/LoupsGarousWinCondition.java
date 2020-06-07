package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.endings.LoupsGarousWonEnding;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;

public final class LoupsGarousWinCondition extends OnlyAliveTeamWinCondition {
    public LoupsGarousWinCondition() {
        super(LGTeams.LOUPS_GAROUS, LoupsGarousWonEnding::new);
    }
}
