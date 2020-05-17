package com.github.df.loupsgarous.endings;

import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.game.PlayerGameOutcome;

public final class EveryoneDeadEnding extends LGEnding {
    @Override
    public String getMessage() {
        return "Tout le monde est mort !";
    }

    @Override
    public PlayerGameOutcome getOutcomeFor(LGPlayer player) {
        return PlayerGameOutcome.LOSE;
    }
}
