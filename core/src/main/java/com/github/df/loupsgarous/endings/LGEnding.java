package com.github.df.loupsgarous.endings;

import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.game.PlayerGameOutcome;

public abstract class LGEnding {
    public abstract String getMessage();

    public abstract PlayerGameOutcome getOutcomeFor(LGPlayer player);

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
