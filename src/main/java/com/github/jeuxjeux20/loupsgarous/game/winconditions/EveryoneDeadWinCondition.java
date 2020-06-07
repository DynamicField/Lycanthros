package com.github.jeuxjeux20.loupsgarous.game.winconditions;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.endings.EveryoneDeadEnding;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;

import java.util.Optional;

public final class EveryoneDeadWinCondition implements WinCondition {
    @Override
    public Optional<LGEnding> check(LGGameOrchestrator orchestrator) {
        boolean isSomeoneAlive = orchestrator.game().getAlivePlayers().findAny().isPresent();
        if (!isSomeoneAlive) {
            return Optional.of(new EveryoneDeadEnding());
        } else {
            return Optional.empty();
        }
    }
}
