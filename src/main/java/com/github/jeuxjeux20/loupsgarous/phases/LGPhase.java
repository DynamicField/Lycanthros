package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.SafeCast;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorDependent;
import me.lucko.helper.terminable.TerminableConsumer;

import javax.annotation.Nonnull;

/**
 * Phases are a fundamental aspect of the game, they split the game into multiple parts
 * (like in the real tabletop game) and are ran sequentially, in a loop, until
 * something ends the game (like a team winning, for example).
 * <p>
 * The runnable implementation of this interface is {@link RunnableLGPhase}, or
 * its synchronous counterpart, {@link LogicLGPhase}.
 * <p>
 * They implement {@link TerminableConsumer} to terminate objects when
 * the phase finishes executing.
 * <p>
 * The {@link LGPhasesModule} contains all the phases of the classic game.
 */
public interface LGPhase extends SafeCast, TerminableConsumer, OrchestratorDependent {
    /**
     * Defines if the phase is considered as being used for game logic only,
     * and do not require any user interaction.
     * <p>
     * One example of a game logic would be {@link RevealAllKillsPhase}.
     * <p>
     * Note: No boss bar is shown for game logic phases.
     *
     * @return whether or not this game is used for game logic only
     */
    default boolean isLogic() {
        return false;
    }

    /**
     * Stops this phase if it is running.
     *
     * @return {@code true} if the phase stopped, or {@code false} if it didn't
     */
    boolean stop();

    /**
     * Gets the game orchestrator that this phase is linked to.
     *
     * @return the game orchestrator
     */
    @Override
    LGGameOrchestrator gameOrchestrator();

    /**
     * The null object for a phase.
     */
    class Null implements LGPhase {
        private final LGGameOrchestrator orchestrator;

        public Null(LGGameOrchestrator orchestrator) {
            this.orchestrator = orchestrator;
        }

        @Override
        public LGGameOrchestrator gameOrchestrator() {
            return orchestrator;
        }

        @Override
        public boolean stop() {
            return false;
        }

        @Nonnull
        @Override
        public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
            return terminable;
        }
    }
}
