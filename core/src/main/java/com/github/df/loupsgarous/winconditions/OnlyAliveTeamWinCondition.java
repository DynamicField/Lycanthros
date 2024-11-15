package com.github.df.loupsgarous.winconditions;

import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.github.df.loupsgarous.endings.LGEnding;
import com.github.df.loupsgarous.teams.LGTeam;

import java.util.Optional;
import java.util.function.Supplier;

public class OnlyAliveTeamWinCondition implements WinCondition {
    private final LGTeam team;
    private final Supplier<LGEnding> endingSupplier;

    public OnlyAliveTeamWinCondition(LGTeam team, Supplier<LGEnding> endingSupplier) {
        this.team = team;
        this.endingSupplier = endingSupplier;
    }

    @Override
    public Optional<LGEnding> check(LGGameOrchestrator orchestrator) {
        if (orchestrator.getAlivePlayers().allMatch(this::allSameTeam)) {
            LGEnding ending = endingSupplier.get();
            return Optional.of(ending);
        }
        return Optional.empty();
    }

    private boolean allSameTeam(LGPlayer x) {
        return x.teams().get().stream().allMatch(n -> n.equals(team));
    }
}
