package com.github.df.loupsgarous.endings;

import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.game.PlayerGameOutcome;
import com.github.df.loupsgarous.teams.LGTeams;

public final class VillageWonEnding extends LGEnding {
    @Override
    public String getMessage() {
        return "Le village a gagné !";
    }

    @Override
    public PlayerGameOutcome getOutcomeFor(LGPlayer player) {
        return PlayerGameOutcome.wonWhen(player.teams().has(LGTeams.VILLAGEOIS));
    }
}
