package com.github.jeuxjeux20.loupsgarous.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.kill.causes.LGKillCause;
import me.lucko.helper.terminable.Terminable;

import java.util.Arrays;
import java.util.Collection;

import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.STARTED;

public class ActualKillsOrchestrator implements KillsOrchestrator, Terminable {
    private final PendingKillRegistry pendingKillRegistry;
    private final LGGameOrchestrator orchestrator;
    private final PlayerKiller playerKiller;

    public ActualKillsOrchestrator(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
        this.playerKiller = new PlayerKiller(orchestrator);
        this.pendingKillRegistry = new PendingKillRegistry(orchestrator, playerKiller);
    }

    @Override
    public PendingKillRegistry pending() {
        orchestrator.getState().mustBe(STARTED);

        return pendingKillRegistry;
    }

    @Override
    public void instantly(Collection<LGKill> kills) {
        orchestrator.getState().mustBe(STARTED);

        playerKiller.applyKills(kills);
    }

    @Override
    public void instantly(LGKill... kills) {
        instantly(Arrays.asList(kills));
    }

    @Override
    public void instantly(LGPlayer victim, LGKillCause cause) {
        instantly(LGKill.of(victim, cause));
    }

    @Override
    public void close() {
    }
}
