package com.github.jeuxjeux20.loupsgarous.game.teams.revealers;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import java.util.Set;

public final class TeamRevealerAggregator implements TeamRevealer {
    private final Set<TeamRevealer> teamRevealers;

    @Inject
    TeamRevealerAggregator(Set<TeamRevealer> teamRevealers) {
        this.teamRevealers = teamRevealers;
    }

    @Override
    public ImmutableSet<LGTeam> getTeamsRevealed(LGPlayer viewer, LGPlayer playerToReveal, LGGame game) {
        return teamRevealers.stream()
                .flatMap(x -> x.getTeamsRevealed(viewer, playerToReveal, game).stream())
                .filter(playerToReveal.getCard().getTeams()::contains)
                .collect(ImmutableSet.toImmutableSet());
    }
}
