package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

public abstract class EphemeralLogicGameStage extends LogicLGGameStage {
    public EphemeralLogicGameStage(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public boolean isTemporary() {
        return true;
    }
}
