package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.LGKillCause;

import java.util.Arrays;
import java.util.Collection;

/**
 * Contains functionality for killing players with a reason. That sounds absolutely fine.
 */
public interface LGKillsOrchestrator extends LGGameOrchestratorDependent {
    /**
     * Gets the pending kills of the game.
     *
     * @return the pending kills
     */
    PendingKillRegistry pending();

    /**
     * Instantly kills all the victims of the given kills.
     *
     * @param kills the kills to apply
     * @see LGKillEvent
     */
    void instantly(Collection<LGKill> kills);

    /**
     * Instantly kills all the victims of the given kills.
     *
     * @param kills the kills to apply
     * @see LGKillEvent
     */
    default void instantly(LGKill... kills) {
        instantly(Arrays.asList(kills));
    }

    /**
     * Instantly kills the given victim with the given cause.
     *
     * @param victim the victim to kill
     * @param cause  the cause of victim's death
     * @see LGKillEvent
     */
    default void instantly(LGPlayer victim, LGKillCause cause) {
        instantly(LGKill.of(victim, cause));
    }
}
