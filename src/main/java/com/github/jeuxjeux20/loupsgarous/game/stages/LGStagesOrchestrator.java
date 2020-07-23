package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.stages.descriptor.LGStageDescriptor;
import com.github.jeuxjeux20.loupsgarous.game.stages.overrides.StageOverride;

/**
 * This is a component of {@link LGGameOrchestrator} managing the {@linkplain LGStage stages}
 * of a game.
 */
public interface LGStagesOrchestrator extends OrchestratorComponent {
    /**
     * Insert the given stage factory to the current game that
     * will be created and run as soon as possible (LIFO).
     *
     * @param stageFactory the stage factory to insert
     */
    void insert(RunnableLGStage.Factory<?> stageFactory);

    /**
     * Cancels the current stages, if any, and runs the next one.
     * <p>
     * Note that some {@link StageOverride}s might prevent the execution of the next stage,
     * for example, if the game is {@linkplain LGGameState#WAITING_FOR_PLAYERS waiting for players},
     * this method will ensure that the current stage is an instance of {@link GameStartStage}.
     */
    void next();

    /**
     * Gets the current stage, or an instance of {@link LGStage.Null} if there isn't any stage running
     * right now.
     *
     * @return the current stage, or {@link LGStage.Null}
     */
    LGStage current();

    /**
     * Returns the descriptor registry.
     * @return the descriptor registry
     */
    LGStageDescriptor.Registry descriptors();
}
