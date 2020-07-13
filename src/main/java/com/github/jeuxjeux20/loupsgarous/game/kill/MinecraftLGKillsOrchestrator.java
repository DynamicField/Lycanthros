package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.google.inject.Inject;
import me.lucko.helper.Events;

import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.STARTED;

@OrchestratorScoped
public class MinecraftLGKillsOrchestrator implements LGKillsOrchestrator {
    private final MutableLGGameOrchestrator gameOrchestrator;
    private final PendingKillRegistry pendingKillRegistry;
    private final PlayerKiller playerKiller;

    @Inject
    MinecraftLGKillsOrchestrator(MutableLGGameOrchestrator gameOrchestrator,
                                 PendingKillRegistry pendingKillRegistry,
                                 PlayerKiller playerKiller) {
        this.gameOrchestrator = gameOrchestrator;
        this.pendingKillRegistry = pendingKillRegistry;
        this.playerKiller = playerKiller;
    }

    @Override
    public PendingKillRegistry pending() {
        gameOrchestrator.state().mustBe(STARTED);

        return pendingKillRegistry;
    }

    @Override
    public void instantly(LGKill kill) {
        gameOrchestrator.state().mustBe(STARTED);

        playerKiller.killPlayer(kill);

        Events.call(new LGKillEvent(gameOrchestrator, kill));
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return gameOrchestrator;
    }
}
