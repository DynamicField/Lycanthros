package com.github.jeuxjeux20.loupsgarous.endings;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.PlayerGameOutcome;

public abstract class LGEnding {
    public abstract String getMessage();

    public abstract PlayerGameOutcome getOutcomeFor(LGPlayer player);
}
