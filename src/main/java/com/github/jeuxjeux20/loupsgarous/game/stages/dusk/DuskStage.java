package com.github.jeuxjeux20.loupsgarous.game.stages.dusk;

import com.github.jeuxjeux20.loupsgarous.ComponentBased;
import com.github.jeuxjeux20.loupsgarous.LGChatStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.stages.CountdownLGStage;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.boss.BarColor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class    DuskStage extends CountdownLGStage {
    private final List<Action> actionsToRun;

    @Inject
    DuskStage(@Assisted LGGameOrchestrator orchestrator, Set<Action> allActions) {
        super(orchestrator);

        for (Action action : allActions) {
            action.initialize(orchestrator);
        }
        actionsToRun = allActions.stream().filter(x -> x.shouldRun(orchestrator)).collect(Collectors.toList());
    }

    @Override
    protected Countdown createCountdown() {
        return Countdown.of(30);
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.turn().getTime() == LGGameTurnTime.NIGHT && !actionsToRun.isEmpty();
    }

    @Override
    protected void start() {
        for (Action action : actionsToRun) {
            orchestrator.chat().sendToEveryone(LGChatStuff.importantInfo(action.getMessage()));

            action.onDuskStart(orchestrator);
        }
    }

    @Override
    protected void finish() {
        for (Action action : actionsToRun) {
            action.onDuskEnd(orchestrator);
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

    @Override
    public Iterable<? extends Action> getAllComponents() {
        return actionsToRun;
    }

    public abstract static class Action implements ComponentBased {
        abstract protected boolean shouldRun(LGGameOrchestrator orchestrator);

        protected void initialize(LGGameOrchestrator orchestrator) {
        }

        abstract protected String getMessage();

        protected void onDuskStart(LGGameOrchestrator orchestrator) {
        }

        protected void onDuskEnd(LGGameOrchestrator orchestrator) {
        }
    }
}
