package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.event.CountdownTickEvent;

public final class StageEventUtils {
    private StageEventUtils() {
    }

    public static boolean isCurrentStageCountdownEvent(LGGameOrchestrator orchestrator, CountdownTickEvent event) {
        LGStage stage = orchestrator.stages().current();

        return stage instanceof CountdownTimedStage &&
               ((CountdownTimedStage) stage).getCountdown() == event.getCountdown();
    }
}
