package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;

import java.util.stream.Stream;

public class WinConditionChecker {
    public static boolean onlyTeamPresent(Stream<? extends LGPlayer> alivePlayers, String team) {
        return alivePlayers.allMatch(x -> x.getCard().getTeams().stream().allMatch(n -> n.equals(team)));
    }
}
