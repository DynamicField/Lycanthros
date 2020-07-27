package com.github.jeuxjeux20.loupsgarous.winconditions;

import com.github.jeuxjeux20.loupsgarous.endings.LoupsGarousWonEnding;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;

public final class LoupsGarousWinCondition extends OnlyAliveTeamWinCondition {
    public LoupsGarousWinCondition() {
        super(LGTeams.LOUPS_GAROUS, LoupsGarousWonEnding::new);
    }
}
