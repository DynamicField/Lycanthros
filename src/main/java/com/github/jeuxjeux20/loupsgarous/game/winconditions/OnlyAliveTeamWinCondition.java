package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;

import java.util.Optional;
import java.util.function.Supplier;

public class OnlyAliveTeamWinCondition implements WinCondition {
    private final String team;
    private final Supplier<LGEnding> endingSupplier;

    public OnlyAliveTeamWinCondition(String team, Supplier<LGEnding> endingSupplier) {
        this.team = team;
        this.endingSupplier = endingSupplier;
    }

    @Override
    public Optional<LGEnding> check(LGGameOrchestrator orchestrator) {
        if (orchestrator.getGame().getAlivePlayers().allMatch(this::allSameTeam)) {
            LGEnding ending = endingSupplier.get();
            return Optional.of(ending);
        }
        return Optional.empty();
    }

    private boolean allSameTeam(LGPlayer x) {
        return x.getCard().getTeams().stream().allMatch(n -> n.equals(team));
    }
}
