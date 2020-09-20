package com.github.jeuxjeux20.loupsgarous.endings;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.PlayerGameOutcome;

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
