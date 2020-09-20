package com.github.jeuxjeux20.loupsgarous.winconditions;

import com.github.jeuxjeux20.loupsgarous.endings.EveryoneDeadEnding;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;

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
