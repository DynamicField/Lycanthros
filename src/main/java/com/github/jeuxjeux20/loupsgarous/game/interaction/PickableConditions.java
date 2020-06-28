package com.github.jeuxjeux20.loupsgarous.game.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.interaction.condition.FunctionalPickConditions;

public final class PickableConditions {
    private PickableConditions() {}

    /**
     * Ensure that the target to kill is alive.
     * @param builder the builder to add the checks to
     */
    public static void ensureKillTargetAlive(FunctionalPickConditions.Builder<LGPlayer> builder) {
        builder.ensureTarget(LGPlayer::isAlive, "Impossible de tuer un joueur mort.");
    }
}
