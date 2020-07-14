package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
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
            MutableLGPlayer whoDied = orchestrator.game().ensurePresent(kill.getVictim());

            Preconditions.checkArgument(whoDied.isAlive(),
                    "Cannot kill player " + whoDied.getName() + " because they are dead.");

            whoDied.setDead(true);
        }

        Events.call(new LGKillEvent(orchestrator, kills));
    }
}
