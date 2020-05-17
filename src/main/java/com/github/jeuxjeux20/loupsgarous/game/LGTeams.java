package com.github.jeuxjeux20.loupsgarous.game;

import java.util.UUID;

public final class LGTeams {
    public static final String GRAY_AREA = "GRAY_AREA";
    public static final String LOUPS_GAROUS = "LOUPS_GAROUS";
    public static final String VILLAGEOIS = "VILLAGEOIS";

    public static final String COUPLE_PREFIX = "COUPLE_";

    private LGTeams() {
    }

    /**
     * Creates a new couple team, using the {@link #COUPLE_PREFIX} and appends a randomly generated {@link UUID}.
     *
     * @return the team string of the couple
     */
    public static String newCouple() {
        return COUPLE_PREFIX + UUID.randomUUID();
    }

    public static boolean isCouple(String team) {
        return team.startsWith(COUPLE_PREFIX);
    }
}
