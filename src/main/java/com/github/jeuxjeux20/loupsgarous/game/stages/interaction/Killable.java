package com.github.jeuxjeux20.loupsgarous.game.stages.interaction;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.condition.FunctionalPickConditions;

public interface Killable extends Pickable<LGPlayer> {
    /**
     * Adds basic checks to the specified builder. These include: checking if the player is alive.
     * @param builder the builder to add the checks to
     */
    static void addBasicChecks(FunctionalPickConditions.Builder<LGPlayer> builder) {
        builder.ensureTarget(LGPlayer::isAlive, "Impossible de tuer un joueur mort.");
    }
}
