package com.github.df.loupsgarous.endings;

import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.game.PlayerGameOutcome;
import com.github.df.loupsgarous.teams.CoupleTeam;

public final class CoupleWonEnding extends LGEnding {
    private final CoupleTeam coupleTeam;

    public CoupleWonEnding(CoupleTeam coupleTeam) {
        this.coupleTeam = coupleTeam;
    }

    @Override
    public String getMessage() {
        return "Le couple a gagné !";
    }

    @Override
    public PlayerGameOutcome getOutcomeFor(LGPlayer player) {
        return PlayerGameOutcome.wonWhen(player.teams().has(coupleTeam));
    }
}
