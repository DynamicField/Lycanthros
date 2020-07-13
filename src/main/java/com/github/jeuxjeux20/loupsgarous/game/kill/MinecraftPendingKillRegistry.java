package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.LGKillReason;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import me.lucko.helper.Events;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.STARTED;

@OrchestratorScoped
public class MinecraftPendingKillRegistry implements PendingKillRegistry {
    private final LGGameOrchestrator orchestrator;
    private final PlayerKiller playerKiller;

    private final Map<LGPlayer, LGKillReason> kills = new HashMap<>();

    @Inject
    MinecraftPendingKillRegistry(LGGameOrchestrator orchestrator, PlayerKiller playerKiller) {
        this.orchestrator = orchestrator;
        this.playerKiller = playerKiller;
    }

    @Override
    public ImmutableSet<LGKill> getAll() {
        return kills.entrySet().stream()
                .map(e -> LGKill.of(e.getKey(), e.getValue()))
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public Optional<LGKillReason> get(LGPlayer player) {
        return Optional.ofNullable(kills.get(player));
    }

    @Override
    public void put(LGPlayer player, LGKillReason killReason) {
        kills.put(player, killReason);
    }

    @Override
    public boolean remove(LGPlayer player) {
        return kills.remove(player) != null;
    }

    @Override
    public boolean contains(LGPlayer player) {
        return kills.containsKey(player);
    }

    @Override
    public boolean isEmpty() {
        return kills.isEmpty();
    }

    @Override
    public void reveal() {
        orchestrator.state().mustBe(STARTED);

        ImmutableSet<LGKill> kills = getAll();

        for (LGKill kill : kills) {
            if (kill.getWhoDied().isAlive()) {
                playerKiller.killPlayer(kill);
            }
        }

        Events.call(new LGKillEvent(orchestrator, kills));
    }
}
