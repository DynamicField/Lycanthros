package com.github.jeuxjeux20.loupsgarous.kill;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.kill.causes.LGKillCause;
import com.google.inject.Inject;

import java.util.Arrays;
import java.util.Collection;

import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.STARTED;

@OrchestratorScoped
public class LGKillsOrchestrator extends AbstractOrchestratorComponent {
    private final PendingKillRegistry pendingKillRegistry;
    private final PlayerKiller playerKiller;

    @Inject
    LGKillsOrchestrator(LGGameOrchestrator orchestrator,
                        PendingKillRegistry pendingKillRegistry,
                        PlayerKiller playerKiller) {
        super(orchestrator);
        this.pendingKillRegistry = pendingKillRegistry;
        this.playerKiller = playerKiller;
    }

    /**
     * Gets the pending kills of the game.
     *
     * @return the pending kills
     */
    public PendingKillRegistry pending() {
        orchestrator.state().mustBe(STARTED);

        return pendingKillRegistry;
    }

    /**
     * Instantly kills all the victims of the given kills.
     *
     * @param kills the kills to apply
     * @see LGKillEvent
     */
    public void instantly(Collection<LGKill> kills) {
        orchestrator.state().mustBe(STARTED);

        playerKiller.applyKills(kills);
    }

    /**
     * Instantly kills all the victims of the given kills.
     *
     * @param kills the kills to apply
     * @see LGKillEvent
     */
    public void instantly(LGKill... kills) {
        instantly(Arrays.asList(kills));
    }

    /**
     * Instantly kills the given victim with the given cause.
     *
     * @param victim the victim to kill
     * @param cause  the cause of victim's death
     * @see LGKillEvent
     */
    public void instantly(LGPlayer victim, LGKillCause cause) {
        instantly(LGKill.of(victim, cause));
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }
}
