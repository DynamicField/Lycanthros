package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorAware;
import com.github.jeuxjeux20.loupsgarous.storage.MapStorage;
import com.github.jeuxjeux20.loupsgarous.storage.Storage;
import com.github.jeuxjeux20.loupsgarous.storage.StorageProvider;
import me.lucko.helper.terminable.TerminableConsumer;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

/**
 * Phases are a fundamental aspect of the game, they split the game into multiple parts (like in the
 * real tabletop game) and are ran sequentially, in a loop, until something ends the game (like a
 * team winning, for example).
 * <p>
 * The runnable implementation of this interface is {@link RunnablePhase}.
 * <p>
 * They implement {@link TerminableConsumer} to terminate objects when the phase finishes
 * executing.
 */
public interface Phase extends TerminableConsumer, OrchestratorAware, StorageProvider {
    /**
     * Defines if the phase is considered as being used for game logic only, and do not require any
     * user interaction.
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

    boolean stop();

    /**
     * Attempts to interrupt this phase.
     *
     * @return {@code true} if the phase interrupted successfully, or else {@code false}
     */
    boolean interrupt();

    State getState();

    @Nullable PhaseTerminationMethod getTerminationMethod();

    PhaseDescriptor getDescriptor();

    @Override
    LGGameOrchestrator getOrchestrator();

    enum State {
        READY(true),
        PREPARING(true),
        RUNNING(true),
        ENDING(false),
        TERMINATED(false) {
            @Override
            public boolean isReplaceable() {
                return true;
            }
        };

        private final boolean interruptionPossible;

        State(boolean interruptionPossible) {
            this.interruptionPossible = interruptionPossible;
        }

        public boolean isInterruptionPossible() {
            return interruptionPossible;
        }

        public boolean isReplaceable() {
            return interruptionPossible;
        }
    }

    /**
     * The null object for a phase.
     */
    class Null implements Phase {
        private final LGGameOrchestrator orchestrator;
        private final Storage storage = new MapStorage();

        public Null(LGGameOrchestrator orchestrator) {
            this.orchestrator = orchestrator;
        }

        @Override
        public LGGameOrchestrator getOrchestrator() {
            return orchestrator;
        }

        public boolean stop() {
            return false;
        }

        @Override
        public boolean interrupt() {
            return false;
        }

        @Override
        public State getState() {
            return State.TERMINATED;
        }

        @Override
        public @Nullable PhaseTerminationMethod getTerminationMethod() {
            return PhaseTerminationMethod.NOT_RAN;
        }

        @Override
        public PhaseDescriptor getDescriptor() {
            return PhaseDescriptor.fromClass(getClass());
        }

        @Override
        public Storage getStorage() {
            return storage;
        }

        @Nonnull
        @Override
        public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
            return terminable;
        }
    }
}
