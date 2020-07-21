package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import me.lucko.helper.Events;

import java.util.Collection;

/**
 * Internal class to kill players. This sounds ASTONISHINGLY fine.
 */
@OrchestratorScoped
final class PlayerKiller {
    private final MutableLGGameOrchestrator orchestrator;

    @Inject
    PlayerKiller(MutableLGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    void applyKills(Collection<LGKill> kills) {
        for (LGKill kill : kills) {
            InternalLGPlayer victim = orchestrator.game().ensurePresent(kill.getVictim());

            Preconditions.checkArgument(victim.isAlive(),
                    "Cannot kill player " + victim.getName() + " because they are dead.");

            victim.dieSilently();
        }

        Events.call(new LGKillEvent(orchestrator, kills));
    }
}
