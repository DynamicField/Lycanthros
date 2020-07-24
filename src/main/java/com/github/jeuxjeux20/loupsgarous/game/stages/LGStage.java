package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.SafeCast;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorDependent;
import me.lucko.helper.terminable.TerminableConsumer;

import javax.annotation.Nonnull;

/**
 * Stages are a fundamental aspect of the game, they split the game into multiple parts
 * (like in the real tabletop game) and are ran sequentially, in a loop, until
 * something ends the game (like a team winning, for example).
 * <p>
 * The runnable implementation of this interface is {@link RunnableLGStage}, or
 * its synchronous counterpart, {@link LogicLGStage}.
 * <p>
 * They implement {@link TerminableConsumer} to terminate objects when
 * the stage finishes executing.
 * <p>
 * They can be used with a {@link StagesModule}, or by
 * using {@link LGStagesOrchestrator#insert(RunnableLGStage.Factory)}.
 * <p>
 * The {@link LGStagesModule} contains all the stages of the classic game.
 */
public interface LGStage extends SafeCast, TerminableConsumer, OrchestratorDependent {
    /**
     * Defines if the stage is considered as being used for game logic only,
     * and do not require any user interaction.
     * <p>
     * One example of a game logic would be {@link RevealAllKillsStage}.
     * <p>
     * Note: No boss bar is shown for game logic stages.
     *
     * @return whether or not this game is used for game logic only
     */
    default boolean isLogic() {
        return false;
    }

    /**
     * Stops this stage if it is running.
     *
     * @return {@code true} if the stage stopped, or {@code false} if it didn't
     */
    boolean stop();

    /**
     * Gets the game orchestrator that this stage is linked to.
     *
     * @return the game orchestrator
     */
    @Override
    LGGameOrchestrator gameOrchestrator();

    /**
     * The null object for a stage.
     */
    class Null implements LGStage {
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
