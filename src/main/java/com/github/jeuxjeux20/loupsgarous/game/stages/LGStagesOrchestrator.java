package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorComponent;
import org.jetbrains.annotations.NotNull;

public interface LGStagesOrchestrator extends LGGameOrchestratorComponent {
    /**
     * Adds a stage to the current game.
     * If the game has started, the stage added will be executed as soon as possible,
     * else, it will be added at the end.
     *
     * @param stage the stage to add
     */
    void add(AsyncLGGameStage.Factory<?> stage);

    void next();

    @NotNull LGGameStage current();

    interface Factory {
        LGStagesOrchestrator create(LGGameOrchestrator gameOrchestrator);
    }
}
