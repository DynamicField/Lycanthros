package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.events.LGKillEvent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.HashSet;
import java.util.Set;

import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.STARTED;

public class MinecraftLGKillsOrchestrator implements LGKillsOrchestrator {
    private final MutableLGGameOrchestrator gameOrchestrator;

    private final Set<LGKill> pendingKills = new HashSet<>();

    @Inject
    MinecraftLGKillsOrchestrator(@Assisted MutableLGGameOrchestrator gameOrchestrator) {
        this.gameOrchestrator = gameOrchestrator;
    }

    @Override
    public Set<LGKill> pending() {
        gameOrchestrator.state().mustBe(STARTED);

        return pendingKills;
    }

    @Override
    public void revealPending() {
        gameOrchestrator.state().mustBe(STARTED);

        ImmutableList<LGKill> kills = ImmutableList.copyOf(pendingKills);
        pendingKills.clear();

        for (LGKill kill : kills) {
            killPlayer(kill);
        }

        gameOrchestrator.callEvent(new LGKillEvent(gameOrchestrator, kills));
    }

    @Override
    public void instantly(LGKill kill) {
        gameOrchestrator.state().mustBe(STARTED);

        killPlayer(kill);

        gameOrchestrator.callEvent(new LGKillEvent(gameOrchestrator, kill));
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return gameOrchestrator;
    }

    private void killPlayer(LGKill kill) {
        MutableLGPlayer whoDied = gameOrchestrator.game().getPlayer(kill.getWhoDied())
                .orElseThrow(() -> new IllegalArgumentException(
                        "The player" + kill.getWhoDied().getName() + " is not present in the game's players."
                ));

        Preconditions.checkArgument(whoDied.isAlive(),
                "Cannot kill player " + whoDied.getName() + " because they are dead.");

        whoDied.setDead(true);
    }
}
