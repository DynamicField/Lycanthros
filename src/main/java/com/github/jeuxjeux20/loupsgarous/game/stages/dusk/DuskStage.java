package com.github.jeuxjeux20.loupsgarous.game.stages.dusk;

import com.github.jeuxjeux20.loupsgarous.LGChatStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.stages.CountdownLGStage;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.bukkit.boss.BarColor;

import java.util.Set;

public class DuskStage extends CountdownLGStage {
    private final ImmutableList<DuskAction> actionsToRun;

    @Inject
    DuskStage(LGGameOrchestrator orchestrator, Set<DuskAction> actions) {
        super(orchestrator);

        ImmutableList.Builder<DuskAction> actionsToRunBuilder = ImmutableList.builder();

        for (DuskAction action : actions) {
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
        return orchestrator.game().getTurn().getTime() == LGGameTurnTime.NIGHT &&
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

    @Override
    public String getName() {
        return "Crépuscule";
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.PURPLE;
    }

    public ImmutableList<DuskAction> getActions() {
        return actionsToRun;
    }

    private void closeAllActions() {
        actionsToRun.forEach(DuskAction::closeAndReportException);
    }

}
