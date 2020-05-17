package com.github.df.loupsgarous.phases.dusk;

import com.github.df.loupsgarous.Countdown;
import com.github.df.loupsgarous.chat.LGChatStuff;
import com.github.df.loupsgarous.extensibility.ContentFactory;
import com.github.df.loupsgarous.extensibility.registry.GameRegistries;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGGameTurnTime;
import com.github.df.loupsgarous.phases.CountdownPhase;
import com.github.df.loupsgarous.phases.PhaseColor;
import com.github.df.loupsgarous.phases.PhaseInfo;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

@PhaseInfo(name = "Crépuscule", color = PhaseColor.PURPLE)
public final class DuskPhase extends CountdownPhase {
    private final List<DuskAction> actionsToRun = new ArrayList<>();

    public DuskPhase(LGGameOrchestrator orchestrator) {
        super(orchestrator);

        for (ContentFactory<? extends DuskAction> actionFactory :
                GameRegistries.DUSK_ACTIONS.get(orchestrator)) {
            DuskAction action = actionFactory.create(orchestrator);

            if (action.shouldRun()) {
                actionsToRun.add(action);
            } else {
                action.closeAndReportException();
            }
        }

        actionsToRun.forEach(this::bind);
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
        return ImmutableList.copyOf(actionsToRun);
    }
}
