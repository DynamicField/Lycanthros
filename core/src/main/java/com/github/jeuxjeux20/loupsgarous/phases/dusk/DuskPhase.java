package com.github.jeuxjeux20.loupsgarous.phases.dusk;

import com.github.jeuxjeux20.loupsgarous.Countdown;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff;
import com.github.jeuxjeux20.loupsgarous.extensibility.ContentFactory;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.phases.CountdownLGPhase;
import com.github.jeuxjeux20.loupsgarous.phases.PhaseColor;
import com.github.jeuxjeux20.loupsgarous.phases.PhaseInfo;
import com.google.common.collect.ImmutableList;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.DUSK_ACTIONS;

@PhaseInfo(name = "Crépuscule", color = PhaseColor.PURPLE)
public final class DuskPhase extends CountdownLGPhase {
    private final ImmutableList<DuskAction> actionsToRun;

    public DuskPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        ImmutableList.Builder<DuskAction> actionsToRunBuilder = ImmutableList.builder();

        for (ContentFactory<? extends DuskAction> actionFactory :
                DUSK_ACTIONS.getContents(orchestrator)) {
            DuskAction action = actionFactory.create(orchestrator);

            if (action.shouldRun()) {
                actionsToRunBuilder.add(action);
            } else {
                action.closeAndReportException();
            }
        }

        actionsToRun = actionsToRunBuilder.build();

        bind(this::closeAllActions);
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.getTurn().getTime() == LGGameTurnTime.NIGHT &&
               !actionsToRun.isEmpty();
    }

    @Override
    protected void start() {
        for (DuskAction action : actionsToRun) {
            orchestrator.chat().sendToEveryone(LGChatStuff.importantInfo(action.getMessage()));

            action.onDuskStart();
        }
    }

    @Override
    protected void finish() {
        for (DuskAction action : actionsToRun) {
            action.onDuskEnd();
        }
    }

    public ImmutableList<DuskAction> getActions() {
        return actionsToRun;
    }

    private void closeAllActions() {
        actionsToRun.forEach(DuskAction::closeAndReportException);
    }
}
