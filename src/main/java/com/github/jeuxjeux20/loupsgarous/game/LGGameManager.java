package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.event.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.lobby.LGGameBootstrapData;
import com.github.jeuxjeux20.loupsgarous.lobby.PlayerJoinException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Singleton
public class LGGameManager {
    private final LGGameOrchestrator.Factory orchestratorFactory;

    private final List<LGGameOrchestrator> ongoingGames = new ArrayList<>();

    private final Map<String, LGGameOrchestrator> gamesById = new HashMap<>();
    private final Map<UUID, LGPlayerAndGame> playerGames = new HashMap<>();

    @Inject
    LGGameManager(LGGameOrchestrator.Factory orchestratorFactory) {
        this.orchestratorFactory = orchestratorFactory;

        Events.subscribe(LGGameDeletedEvent.class)
                .handler(e -> removeDeletedGame(e.getOrchestrator()));

        Events.subscribe(LGPlayerJoinEvent.class, EventPriority.LOWEST)
                .handler(e -> {
                    LGPlayerAndGame value = new LGPlayerAndGame(e.getLGPlayer(), e.getOrchestrator());

                    playerGames.put(e.getPlayer().getUniqueId(), value);
                });

        Events.subscribe(LGPlayerQuitEvent.class, EventPriority.LOWEST)
                .handler(e -> playerGames.remove(e.getPlayerUUID()));

        Events.subscribe(PluginDisableEvent.class)
                .filter(e -> e.getPlugin() instanceof LoupsGarous)
                .handler(e -> getAll().forEach(LGGameOrchestrator::delete));
    }

    public synchronized LGGameOrchestrator start(Player owner, Composition composition, @Nullable String id)
            throws GameCreationException {
        if (id == null) {
            id = UUID.randomUUID().toString().substring(0, 8);
        }

        if (gamesById.containsKey(id)) {
            throw new DuplicateIdentifierException("A game with the id '" + id + "' is already present.");
        }

        LGGameOrchestrator orchestrator = orchestratorFactory.create(
                new LGGameBootstrapData(owner, composition, id)
        );

        orchestrator.initialize();

        ongoingGames.add(orchestrator);
        gamesById.put(id, orchestrator);

        return orchestrator;
    }

    public synchronized final ImmutableList<LGGameOrchestrator> getAll() {
        return ImmutableList.copyOf(ongoingGames);
    }

    public synchronized Optional<LGPlayerAndGame> getPlayerInGame(UUID playerUUID) {
        return Optional.ofNullable(playerGames.get(playerUUID));
    }

    public synchronized Optional<LGPlayerAndGame> getPlayerInGame(Player player) {
        return getPlayerInGame(player.getUniqueId());
    }

    public synchronized Optional<LGGameOrchestrator> get(String id) {
        return Optional.ofNullable(gamesById.get(id));
    }

    public synchronized void joinOrStart(Player player, Composition composition, String id)
            throws GameCreationException, PlayerJoinException {
        Optional<LGGameOrchestrator> existingGame = get(id);

        if (existingGame.isPresent()) {
            existingGame.get().join(player);
        } else {
            start(player, composition, id);
        }
    }

    private synchronized void removeDeletedGame(LGGameOrchestrator orchestrator) {
        Preconditions.checkArgument(orchestrator.getState() == LGGameState.DELETED, "The game must have been deleted.");

        ongoingGames.remove(orchestrator);
        gamesById.remove(orchestrator.getId());
    }
}
