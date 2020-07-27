package com.github.jeuxjeux20.loupsgarous.stages;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;

@StageInfo(name = "Fin !", color = StageColor.YELLOW)
public final class GameEndStage extends CountdownLGStage {
    @Inject
    GameEndStage(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(15);
    }

}
