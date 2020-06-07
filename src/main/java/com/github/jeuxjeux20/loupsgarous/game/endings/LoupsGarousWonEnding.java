package com.github.jeuxjeux20.loupsgarous.game.endings;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.PlayerGameOutcome;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;

public final class LoupsGarousWonEnding extends LGEnding {
    @Override
    public String getMessage() {
        return "Les loups-garous ont gagné !";
    }

    @Override
    public PlayerGameOutcome getOutcomeFor(LGPlayer player) {
        return PlayerGameOutcome.wonWhen(player.getCard().getTeams().contains(LGTeams.LOUPS_GAROUS));
    }
}
