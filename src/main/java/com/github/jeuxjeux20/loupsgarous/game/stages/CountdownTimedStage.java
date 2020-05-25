package com.github.jeuxjeux20.loupsgarous.game.stages;

import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.events.LGTimedStageTickEvent;

public interface CountdownTimedStage extends TimedStage {
    Countdown getCountdown();

    @Override
    default int getSecondsLeft() {
        return getCountdown() == null ? 1 : getCountdown().getTimer();
    }

    @Override
    default int getTotalSeconds() {
        return getCountdown() == null ? 1 : getCountdown().getBiggestTimerValue();
    }

    class TickEventCountdown extends Countdown {
        private final LGGameOrchestrator orchestrator;
        private final CountdownTimedStage me;

        public TickEventCountdown(CountdownTimedStage me, int timerSeconds) {
            super(me.getOrchestrator().getPlugin(), timerSeconds);
            this.orchestrator = me.getOrchestrator();
            this.me = me;
        }

        @Override
        protected final void onTick() {
            plugin.getServer().getPluginManager().callEvent(new LGTimedStageTickEvent(orchestrator, me));
        }
    }
}
