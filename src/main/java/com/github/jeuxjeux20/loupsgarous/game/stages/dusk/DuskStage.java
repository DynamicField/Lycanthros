package com.github.jeuxjeux20.loupsgarous.game.stages.dusk;

import com.github.jeuxjeux20.loupsgarous.ComponentBased;
import com.github.jeuxjeux20.loupsgarous.LGChatStuff;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.stages.CountdownTimedStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.RunnableLGGameStage;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.bukkit.boss.BarColor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DuskStage extends RunnableLGGameStage implements CountdownTimedStage, ComponentBased {
    private final List<Action> actionsToRun;
    private final Countdown countdown;

    @Inject
    DuskStage(@Assisted LGGameOrchestrator orchestrator, Set<Action> allActions) {
        super(orchestrator);

        countdown = Countdown.builder(20)
                .apply(this::addTickEvents)
                .finished(this::runActionsEnd)
                .build(orchestrator);

        for (Action action : allActions) {
            action.initialize(orchestrator);
        }
        actionsToRun = allActions.stream().filter(x -> x.shouldRun(orchestrator)).collect(Collectors.toList());
    }

    @Override
    public boolean shouldRun() {
        return orchestrator.turn().getTime() == LGGameTurnTime.NIGHT && !actionsToRun.isEmpty();
    }

    @Override
    public CompletableFuture<Void> run() {
        for (Action action : actionsToRun) {
            orchestrator.chat().sendToEveryone(LGChatStuff.importantInfo(action.getMessage()));

            action.onDuskStart(orchestrator);
        }

        return countdown.start();
    }

    private void runActionsEnd() {
        for (Action action : actionsToRun) {
            action.onDuskEnd(orchestrator);
        }
    }

    @Override
    public @Nullable String getName() {
        return "Crépuscule";
    }

    @Override
    public BarColor getBarColor() {
        return BarColor.PURPLE;
    }

    @Override
    public Countdown getCountdown() {
        return countdown;
    }

    @Override
    public Iterable<? extends Action> getAllComponents() {
        return actionsToRun;
    }

    public abstract static class Action {
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
