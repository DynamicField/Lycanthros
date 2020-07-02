package com.github.jeuxjeux20.loupsgarous.game.teams.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeams;
import com.google.common.collect.ImmutableSet;

public final class LoupsGarousTeamRevealer implements TeamRevealer {
    @Override
    public ImmutableSet<LGTeam> getTeamsRevealed(LGPlayer viewer, LGPlayer playerToReveal, LGGameOrchestrator orchestrator) {
        if (playerToReveal == viewer) return ImmutableSet.of();

        if (playerToReveal.getCard().getTeams().contains(LGTeams.LOUPS_GAROUS) &&
            viewer.getCard().getTeams().contains(LGTeams.LOUPS_GAROUS)) {
            return ImmutableSet.of(LGTeams.LOUPS_GAROUS);
        }

        return ImmutableSet.of();
    }
}
