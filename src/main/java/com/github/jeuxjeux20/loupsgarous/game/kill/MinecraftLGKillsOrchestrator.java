package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.google.inject.Inject;

import java.util.Collection;

import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.STARTED;

@OrchestratorScoped
public class MinecraftLGKillsOrchestrator
        extends AbstractOrchestratorComponent
        implements LGKillsOrchestrator {
    private final PendingKillRegistry pendingKillRegistry;
    private final PlayerKiller playerKiller;

    @Inject
    MinecraftLGKillsOrchestrator(LGGameOrchestrator orchestrator,
                                 PendingKillRegistry pendingKillRegistry,
                                 PlayerKiller playerKiller) {
        super(orchestrator);
        this.pendingKillRegistry = pendingKillRegistry;
        this.playerKiller = playerKiller;
    }

    @Override
    public PendingKillRegistry pending() {
        orchestrator.state().mustBe(STARTED);

        return pendingKillRegistry;
    }

    @Override
    public void instantly(Collection<LGKill> kills) {
        orchestrator.state().mustBe(STARTED);

        playerKiller.applyKills(kills);
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }
}
