package com.github.df.loupsgarous.winconditions;

import com.github.df.loupsgarous.endings.EveryoneDeadEnding;
import com.github.df.loupsgarous.endings.LGEnding;
import com.github.df.loupsgarous.game.LGGameOrchestrator;

import java.util.Optional;

public final class EveryoneDeadWinCondition implements WinCondition {
    @Override
    public Optional<LGEnding> check(LGGameOrchestrator orchestrator) {
        boolean isSomeoneAlive = orchestrator.getAlivePlayers().findAny().isPresent();
        if (!isSomeoneAlive) {
            return Optional.of(new EveryoneDeadEnding());
        } else {
            return Optional.empty();
        }
    }
}
