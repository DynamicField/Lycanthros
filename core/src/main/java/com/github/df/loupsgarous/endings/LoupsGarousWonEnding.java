package com.github.df.loupsgarous.endings;

import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.game.PlayerGameOutcome;
import com.github.df.loupsgarous.teams.LGTeams;

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
