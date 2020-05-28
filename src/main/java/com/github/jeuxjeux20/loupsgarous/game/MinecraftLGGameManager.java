package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.events.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.github.jeuxjeux20.loupsgarous.util.WordingUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Singleton
class MinecraftLGGameManager implements LGGameManager {
    private final MultiverseCore multiverse;
    private final LoupsGarous plugin;
    private final LGGameOrchestrator.Factory orchestratorFactory;

    private final CopyOnWriteArraySet<Player> playersLocked = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArrayList<LGGameOrchestrator> ongoingGames = new CopyOnWriteArrayList<>();

    private final Hashtable<String, LGGameOrchestrator> gamesById = new Hashtable<>();
    private final Hashtable<UUID, LGGameOrchestrator> gamesByPlayerUUID = new Hashtable<>();

    @Inject
    public MinecraftLGGameManager(MultiverseCore multiverse, LoupsGarous plugin,
                                  LGGameOrchestrator.Factory orchestratorFactory) {
        this.multiverse = multiverse;
        this.plugin = plugin;
        this.orchestratorFactory = orchestratorFactory;

        Events.subscribe(LGGameDeletedEvent.class)
                .handler(e -> removeDeletedGame(e.getOrchestrator()));

        Events.subscribe(LGPlayerJoinEvent.class)
                .handler(e -> gamesByPlayerUUID.put(e.getPlayer().getUniqueId(), e.getOrchestrator()));

        Events.subscribe(LGPlayerQuitEvent.class)
                .handler(e -> gamesByPlayerUUID.remove(e.getPlayerUUID()));
    }

    @Override
    public synchronized SafeResult<LGGameOrchestrator> startGame(Set<Player> players, Composition composition) {
        List<Player> playersLockedWhileStarting = playersLocked.stream().filter(players::contains).collect(Collectors.toList());
        if (!playersLockedWhileStarting.isEmpty()) {
            return SafeResult.error(playersLockedWhileStarting.size() > 1 ?
                    "Les joueurs " + WordingUtils.joiningCommaAnd(playersLocked.stream(), Player::getName) +
                    " sont en train d'accéder à une partie." :
                    "Le joueur " + playersLockedWhileStarting.get(0).getName() + " est en train d'accéder à une partie.");
        }

        playersLocked.addAll(players);
        try {
            Set<Player> presentPlayers = findPresentPlayers(players);

            int presentPlayersCount = presentPlayers.size();
            if (presentPlayersCount > 0) {
                return SafeResult.error(presentPlayersCount > 1 ?
                        "Les joueurs " + presentPlayers.stream().map(Player::getName).collect(Collectors.joining(", ")) +
                        " sont déjà en partie." :
                        "Le joueur " + presentPlayers.iterator().next().getName() + " est déjà en partie.");
            }

            String id = UUID.randomUUID().toString().substring(0, 8);

            LGGameOrchestrator orchestrator = orchestratorFactory.create(
                    new LGGameLobbyInfo(players, composition, null, id)
            );

            ongoingGames.add(orchestrator);
            gamesById.put(id, orchestrator);

            orchestrator.initialize();

            return SafeResult.success(orchestrator);
        } catch (CannotCreateLobbyException e) {
            return SafeResult.error(e.getMessage());
        } finally {
            playersLocked.removeAll(players);
        }
    }

    private Set<Player> findPresentPlayers(Set<Player> players) {
        return ongoingGames.stream().flatMap(x -> x.getGame().getPlayers().stream())
                .map(LGPlayer::getMinecraftPlayer)
                .flatMap(OptionalUtils::stream)
                .filter(players::contains)
                .collect(Collectors.toSet());
    }

    @Override
    public synchronized final ImmutableList<LGGameOrchestrator> getOngoingGames() {
        return ImmutableList.copyOf(ongoingGames);
    }

    @Override
    public Optional<LGPlayerAndGame> getPlayerInGame(UUID playerUUID) {
        LGGameOrchestrator orchestrator = gamesByPlayerUUID.get(playerUUID);
        if (orchestrator == null) return Optional.empty();

        return orchestrator.getGame().getPlayer(playerUUID).map(p -> new LGPlayerAndGame(p, orchestrator));
    }

    @Override
    public Optional<LGGameOrchestrator> getGameById(String id) {
        return Optional.ofNullable(gamesById.get(id));
    }

    private synchronized void removeDeletedGame(LGGameOrchestrator orchestrator) {
        Preconditions.checkArgument(orchestrator.getState() == LGGameState.DELETED, "The game must have been deleted.");
        ongoingGames.remove(orchestrator);
        gamesById.remove(orchestrator.getId());
    }
}
