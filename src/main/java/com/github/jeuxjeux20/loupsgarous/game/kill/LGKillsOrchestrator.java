package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.LGKillReason;

public interface LGKillsOrchestrator extends LGGameOrchestratorDependent {
    PendingKillRegistry pending();

    void instantly(LGKill kill);

    default void instantly(LGPlayer player, LGKillReason reason) {
        instantly(LGKill.of(player, reason));
    }
}
