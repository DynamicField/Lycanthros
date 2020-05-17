package com.github.df.loupsgarous.interaction;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.interaction.condition.FunctionalPickConditions;
import com.github.df.loupsgarous.interaction.condition.FunctionalPickConditions.BidirectionalPlayerPredicate;
import com.github.df.loupsgarous.Check;

public final class PickableConditions {
    private PickableConditions() {
    }

    /**
     * Ensure that the target to kill is alive.
     *
     * @param builder the builder to add the checks to
     */
    public static void ensureKillTargetAlive(FunctionalPickConditions.Builder<LGPlayer> builder) {
        builder.ensureTarget(LGPlayer::isAlive, "Impossible de tuer un joueur mort.");
    }

    /**
     * Returns a predicate that checks in the player is in the given orchestrator's game players.
     *
     * @param orchestrator the orchestrator containing the game
     * @return a {@link BidirectionalPlayerPredicate} that returns a check
     * about the given player presence in the game
     */
    public static BidirectionalPlayerPredicate checkPlayerGamePresence(LGGameOrchestrator orchestrator) {
        return p -> Check.ensure(orchestrator.getPlayers().contains(p), "Vous n'êtes pas dans la partie.");
    }
}
