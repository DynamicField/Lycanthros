package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.teams.CoupleTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.LoupsGarousTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.VillageoisTeam;

public final class LGTeams {
    public static final LGTeam LOUPS_GAROUS = LoupsGarousTeam.INSTANCE;
    public static final LGTeam VILLAGEOIS = VillageoisTeam.INSTANCE;

    private LGTeams() {
    }

    /**
     * Creates a new couple team.
     *
     * @return the new couple
     */
    public static CoupleTeam newCouple() {
        return new CoupleTeam();
    }

    public static boolean isCouple(LGTeam team) {
        return team instanceof CoupleTeam;
    }
}
