package com.github.jeuxjeux20.loupsgarous.endings;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.PlayerGameOutcome;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeams;

public final class LoupsGarousWonEnding extends LGEnding {
    @Override
    public String getMessage() {
        return "Les loups-garous ont gagné !";
    }

    @Override
    public PlayerGameOutcome getOutcomeFor(LGPlayer player) {
        return PlayerGameOutcome.wonWhen(player.teams().has(LGTeams.LOUPS_GAROUS));
    }
}
