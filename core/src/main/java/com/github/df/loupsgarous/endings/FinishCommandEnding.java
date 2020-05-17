package com.github.df.loupsgarous.endings;

import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.game.PlayerGameOutcome;
import org.jetbrains.annotations.Nullable;

public final class FinishCommandEnding extends LGEnding {
    private final @Nullable String message;

    public FinishCommandEnding() {
        this.message = null;
    }

    public FinishCommandEnding(@Nullable String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message == null ? "La partie a été terminée de force." : message;
    }

    @Override
    public PlayerGameOutcome getOutcomeFor(LGPlayer player) {
        return PlayerGameOutcome.SHRUG;
    }
}
