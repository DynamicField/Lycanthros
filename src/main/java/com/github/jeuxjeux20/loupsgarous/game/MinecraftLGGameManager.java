package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGGameBootstrapData;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.server.PluginDisableEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Singleton
class MinecraftLGGameManager implements LGGameManager {
    private final LGGameOrchestrator.Factory orchestratorFactory;

    private final CopyOnWriteArrayList<LGGameOrchestrator> ongoingGames = new CopyOnWriteArrayList<>();

    private final Hashtable<String, LGGameOrchestrator> gamesById = new Hashtable<>();
    private final Hashtable<UUID, LGGameOrchestrator> gamesByPlayerUUID = new Hashtable<>();

    @Inject
    MinecraftLGGameManager(LGGameOrchestrator.Factory orchestratorFactory) {
        this.orchestratorFactory = orchestratorFactory;

        Events.subscribe(LGGameDeletedEvent.class)
                .handler(e -> removeDeletedGame(e.getOrchestrator()));

        Events.subscribe(LGPlayerJoinEvent.class, EventPriority.LOWEST)
                .handler(e -> gamesByPlayerUUID.put(e.getPlayer().getUniqueId(), e.getOrchestrator()));

        Events.subscribe(LGPlayerQuitEvent.class, EventPriority.LOWEST)
                .handler(e -> gamesByPlayerUUID.remove(e.getPlayerUUID()));

        Events.subscribe(PluginDisableEvent.class)
                .filter(e -> e.getPlugin() instanceof LoupsGarous)
                .handler(e -> getAll().forEach(LGGameOrchestrator::delete));
    }

    @Override
    public synchronized LGGameOrchestrator start(Player owner, Composition composition, @Nullable String id)
            throws GameCreationException {
        if (id == null) {
            id = UUID.randomUUID().toString().substring(0, 8);
        }

        if (gamesById.containsKey(id)) {
            throw new DuplicateIdentifierException("A game with the id '" + id + "' is already present.");
        }

        LGGameOrchestrator orchestrator = orchestratorFactory.create(new LGGameBootstrapData(owner, composition, id));

        ongoingGames.add(orchestrator);
        gamesById.put(id, orchestrator);

        orchestrator.initialize();

        return orchestrator;
    }

    @Override
    public synchronized final ImmutableList<LGGameOrchestrator> getAll() {
        return ImmutableList.copyOf(ongoingGames);
    }

    @Override
    public synchronized Optional<LGPlayerAndGame> getPlayerInGame(UUID playerUUID) {
        LGGameOrchestrator orchestrator = gamesByPlayerUUID.get(playerUUID);
        if (orchestrator == null) return Optional.empty();

        return orchestrator.game().getPlayer(playerUUID)
                .filter(LGPlayer::isPresent)
                .map(p -> new LGPlayerAndGame(p, orchestrator));
    }

    @Override
    public synchronized Optional<LGGameOrchestrator> get(String id) {
        return Optional.ofNullable(gamesById.get(id));
    }

    private synchronized void removeDeletedGame(LGGameOrchestrator orchestrator) {
        Preconditions.checkArgument(orchestrator.state() == LGGameState.DELETED, "The game must have been deleted.");

        ongoingGames.remove(orchestrator);
        gamesById.remove(orchestrator.game().getId());
    }
}
