package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorScoped;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.LGKillCause;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.STARTED;

@OrchestratorScoped
public class MinecraftPendingKillRegistry implements PendingKillRegistry {
    private final LGGameOrchestrator orchestrator;
    private final PlayerKiller playerKiller;

    private final Map<LGPlayer, LGKillCause> kills = new HashMap<>();

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
    public Optional<LGKillCause> get(LGPlayer player) {
        return Optional.ofNullable(kills.get(player));
    }

    @Override
    public void add(LGPlayer victim, LGKillCause cause) {
        kills.put(victim, cause);
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

        List<LGKill> applicableKills = kills.stream()
                .filter(LGKill::canTakeEffect)
                .collect(Collectors.toList());

        playerKiller.applyKills(applicableKills);
    }
}
