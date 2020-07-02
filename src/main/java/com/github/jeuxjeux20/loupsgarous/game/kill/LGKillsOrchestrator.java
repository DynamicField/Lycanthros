package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorDependent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.LGKillReason;

import java.util.Set;
import java.util.function.Supplier;

public interface LGKillsOrchestrator extends LGGameOrchestratorDependent {
    Set<LGKill> pending();

    void revealPending();

    default boolean willDie(LGPlayer player) {
        return pending().stream().anyMatch(kill -> kill.getWhoDied() == player);
    }

    void instantly(LGKill kill);

    default void instantly(LGPlayer player, LGKillReason reason) {
        instantly(LGKill.of(player, reason));
    }

    default void instantly(LGPlayer player, Supplier<LGKillReason> reasonSupplier) {
        instantly(LGKill.of(player, reasonSupplier));
    }

    interface Factory {
        LGKillsOrchestrator create(MutableLGGameOrchestrator orchestrator);
    }
}
