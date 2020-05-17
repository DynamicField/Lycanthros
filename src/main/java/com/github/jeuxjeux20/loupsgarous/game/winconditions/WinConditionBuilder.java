package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.stages.EphemeralLogicGameStage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class WinConditionBuilder {
    private final LGGameOrchestrator orchestrator;
    private final Supplier<? extends LGEnding> endingSupplier;

    private final List<Predicate<LGGameOrchestrator>> predicates = new ArrayList<>();

    private WinConditionBuilder(LGGameOrchestrator orchestrator,
                                Supplier<? extends LGEnding> endingSupplier) {
        this.orchestrator = orchestrator;
        this.endingSupplier = endingSupplier;
    }

    public static WinConditionBuilder create(LGGameOrchestrator orchestrator,
                                             Supplier<? extends LGEnding> endingSupplier) {
        return new WinConditionBuilder(orchestrator, endingSupplier);
    }

    public WinConditionBuilder onlyAliveTeamPresent(String team) {
        predicates.add(x -> WinConditionChecker.onlyTeamPresent(x.getGame().getAlivePlayers(), team));
        return this;
    }

    public WinConditionBuilder everyoneDead() {
        predicates.add(x -> x.getGame().getAlivePlayers().count() == 0);
        return this;
    }

    public WinConditionBuilder condition(Predicate<LGGameOrchestrator> predicate) {
        predicates.add(predicate);
        return this;
    }

    public void checkAfterStage() {
        orchestrator.addStage(x -> new EphemeralLogicGameStage(x) {
            @Override
            public void runSync() {
                check();
            }
        });
    }

    public void checkNow() {
        check();
    }

    private void check() {
        if (!orchestrator.isGameRunning()) return;

        for (Predicate<LGGameOrchestrator> predicate : predicates) {
            if (!predicate.test(orchestrator)) return;
        }
        orchestrator.finish(endingSupplier.get());
    }
}
