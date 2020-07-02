package com.github.jeuxjeux20.loupsgarous.game.stages.dusk;

import com.github.jeuxjeux20.loupsgarous.LGChatStuff;
import com.github.jeuxjeux20.loupsgarous.SafeCast;
import com.github.jeuxjeux20.loupsgarous.game.Countdown;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameTurnTime;
import com.github.jeuxjeux20.loupsgarous.game.stages.CountdownLGStage;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.boss.BarColor;

import javax.annotation.Nonnull;
import java.util.Set;

public class DuskStage extends CountdownLGStage {
    private final ImmutableList<Action> actionsToRun;

    @Inject
    DuskStage(@Assisted LGGameOrchestrator orchestrator, Set<Action> allActions) {
        super(orchestrator);

        for (Action action : allActions) {
            action.initialize(orchestrator);
        }
        actionsToRun = allActions.stream()
                .filter(x -> x.shouldRun(orchestrator))
                .collect(ImmutableList.toImmutableList());

        bind(this::closeAllActions);
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

    public ImmutableList<Action> getActions() {
        return actionsToRun;
    }

    private void closeAllActions() {
        actionsToRun.forEach(Action::closeAndReportException);
    }

    public abstract static class Action implements SafeCast, Terminable, TerminableConsumer {
        private final CompositeTerminable terminableRegistry = CompositeTerminable.create();

        abstract protected boolean shouldRun(LGGameOrchestrator orchestrator);

        protected void initialize(LGGameOrchestrator orchestrator) {
        }

        abstract protected String getMessage();

        protected void onDuskStart(LGGameOrchestrator orchestrator) {
        }

        protected void onDuskEnd(LGGameOrchestrator orchestrator) {
        }

        @Nonnull
        @Override
        public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
            return terminableRegistry.bind(terminable);
        }

        @Override
        public final void close() throws CompositeClosingException {
            terminableRegistry.close();
        }
    }
}
