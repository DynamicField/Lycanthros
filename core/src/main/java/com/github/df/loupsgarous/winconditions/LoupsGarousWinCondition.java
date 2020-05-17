package com.github.df.loupsgarous.winconditions;

import com.github.df.loupsgarous.endings.LoupsGarousWonEnding;
import com.github.df.loupsgarous.teams.LGTeams;

public final class LoupsGarousWinCondition extends OnlyAliveTeamWinCondition {
    public LoupsGarousWinCondition() {
        super(LGTeams.LOUPS_GAROUS, LoupsGarousWonEnding::new);
    }
}
