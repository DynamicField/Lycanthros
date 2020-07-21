package com.github.jeuxjeux20.loupsgarous.game.endings;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.PlayerGameOutcome;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;

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
