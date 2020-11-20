package com.github.jeuxjeux20.loupsgarous.kill;

import com.github.jeuxjeux20.loupsgarous.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.kill.causes.LGKillCause;

import java.util.Collection;

public interface KillsOrchestrator {
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
    void instantly(LGKill... kills);

    /**
     * Instantly kills the given victim with the given cause.
     *
     * @param victim the victim to kill
     * @param cause  the cause of victim's death
     * @see LGKillEvent
     */
    void instantly(LGPlayer victim, LGKillCause cause);
}
