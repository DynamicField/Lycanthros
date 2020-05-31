package com.github.jeuxjeux20.loupsgarous.game.endings;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.PlayerGameOutcome;
import com.github.jeuxjeux20.loupsgarous.game.teams.CoupleTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;

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
        return PlayerGameOutcome.wonWhen(player.getCard().getTeams().contains(coupleTeam));
    }
}
