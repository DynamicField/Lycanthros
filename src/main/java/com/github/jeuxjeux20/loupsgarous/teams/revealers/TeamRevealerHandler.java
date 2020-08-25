package com.github.jeuxjeux20.loupsgarous.teams.revealers;

import com.github.jeuxjeux20.loupsgarous.extensibility.ExtensionPointHandler;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.TEAM_REVEALERS;

@OrchestratorScoped
public final class TeamRevealerHandler implements ExtensionPointHandler {
    private final LGGameOrchestrator orchestrator;

    @Inject
    TeamRevealerHandler(LGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public ImmutableSet<LGTeam> getTeamsRevealed(LGPlayer viewer, LGPlayer playerToReveal) {
        return orchestrator.getBundle().contents(TEAM_REVEALERS).stream()
                .flatMap(x -> x.getTeamsRevealed(viewer, playerToReveal, orchestrator).stream())
                .filter(playerToReveal.teams().get()::contains)
                .collect(ImmutableSet.toImmutableSet());
    }
}
