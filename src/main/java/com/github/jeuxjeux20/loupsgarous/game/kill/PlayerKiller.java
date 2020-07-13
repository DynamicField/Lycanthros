package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;

/**
 * Internal class to kill make a player dead.
 */
@OrchestratorScoped
final class PlayerKiller {
    private final MutableLGGameOrchestrator orchestrator;

    @Inject
    PlayerKiller(MutableLGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    void killPlayer(LGKill kill) {
        MutableLGPlayer whoDied = orchestrator.game().ensurePresent(kill.getWhoDied());

        Preconditions.checkArgument(whoDied.isAlive(),
                "Cannot kill player " + whoDied.getName() + " because they are dead.");

        whoDied.setDead(true);
    }
}
