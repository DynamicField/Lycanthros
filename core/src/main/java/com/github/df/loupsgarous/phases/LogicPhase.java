package com.github.df.loupsgarous.phases;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import io.reactivex.rxjava3.core.Completable;

public abstract class LogicPhase extends RunnablePhase {
    public LogicPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);
    }

    @Override
    public final PhaseTask execute() {
        start();
        //noinspection ReactiveStreamsUnusedPublisher
        return new PhaseTask(Completable.complete()) {
            @Override
            public boolean isRunning() {
                return false;
            }

            @Override
            public boolean stop() {
                return false;
            }
        };
    }

    public abstract void start();

    @Override
    public boolean isLogic() {
        return true;
    }
}
