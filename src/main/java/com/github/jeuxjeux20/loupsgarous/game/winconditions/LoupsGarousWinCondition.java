package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.LGTeams;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.endings.LoupsGarousWonEnding;

import java.util.function.Supplier;

public final class LoupsGarousWinCondition extends OnlyAliveTeamWinCondition {
    public LoupsGarousWinCondition() {
        super(LGTeams.LOUPS_GAROUS, LoupsGarousWonEnding::new);
    }
}
