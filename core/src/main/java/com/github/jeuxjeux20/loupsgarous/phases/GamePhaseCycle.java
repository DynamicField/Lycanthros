package com.github.jeuxjeux20.loupsgarous.phases;

import com.github.jeuxjeux20.loupsgarous.extensibility.ContentFactory;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.common.collect.ImmutableList;
import io.reactivex.rxjava3.disposables.Disposable;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.PHASES;

public class GamePhaseCycle extends PhaseCycle {
    private final Disposable updateSubscription;

    public GamePhaseCycle(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        updatePhases();
        updateSubscription = orchestrator.getGameBox().updates()
                .filter(c -> !c.getContentsDiff(PHASES).isEmpty())
                .subscribe(c -> updatePhases());
    }

    private void updatePhases() {
        ImmutableList<ContentFactory<? extends RunnablePhase>> phases =
                PHASES.getContents(orchestrator);

        setPhases(phases);
    }

    @Override
    protected void stopProgram() {
        super.stopProgram();
        updateSubscription.dispose();
    }
}
