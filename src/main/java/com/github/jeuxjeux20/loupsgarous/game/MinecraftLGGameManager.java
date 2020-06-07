package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.Plugin;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.events.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerQuitEvent;
import com.github.jeuxjeux20.loupsgarous.game.lobby.CannotCloneWorldException;
import com.github.jeuxjeux20.loupsgarous.game.lobby.CannotCreateLobbyException;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGGameLobbyInfo;
import com.github.jeuxjeux20.loupsgarous.game.lobby.MaximumWorldCountReachedException;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Hashtable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
class MinecraftLGGameManager implements LGGameManager {
    private final Logger logger;
    private final LGGameOrchestrator.Factory orchestratorFactory;

    private final CopyOnWriteArrayList<LGGameOrchestrator> ongoingGames = new CopyOnWriteArrayList<>();

    private final Hashtable<String, LGGameOrchestrator> gamesById = new Hashtable<>();
    private final Hashtable<UUID, LGGameOrchestrator> gamesByPlayerUUID = new Hashtable<>();

    @Inject
    MinecraftLGGameManager(@Plugin Logger logger, LGGameOrchestrator.Factory orchestratorFactory) {
        this.logger = logger;
        this.orchestratorFactory = orchestratorFactory;

        Events.subscribe(LGGameDeletedEvent.class)
                .handler(e -> removeDeletedGame(e.getOrchestrator()));

        Events.subscribe(LGPlayerJoinEvent.class)
                .handler(e -> gamesByPlayerUUID.put(e.getPlayer().getUniqueId(), e.getOrchestrator()));

        Events.subscribe(LGPlayerQuitEvent.class)
                .handler(e -> gamesByPlayerUUID.remove(e.getPlayerUUID()));
    }

    @Override
    public synchronized SafeResult<LGGameOrchestrator> startGame(Set<Player> players, Composition composition, @Nullable String id) {
        try {
            Set<Player> presentPlayers = findPresentPlayers(players);

            int presentPlayersCount = presentPlayers.size();
            if (presentPlayersCount > 0) {
                return SafeResult.error(presentPlayersCount > 1 ?
                        "Les joueurs " + presentPlayers.stream().map(Player::getName).collect(Collectors.joining(", ")) +
                        " sont déjà en partie." :
                        "Le joueur " + presentPlayers.iterator().next().getName() + " est déjà en partie.");
            }

            if (id == null) {
                id = UUID.randomUUID().toString().substring(0, 8);
            }

            LGGameOrchestrator orchestrator = orchestratorFactory.create(
                    new LGGameLobbyInfo(players, composition, null, id)
            );

            ongoingGames.add(orchestrator);
            gamesById.put(id, orchestrator);

            orchestrator.initialize();

            return SafeResult.success(orchestrator);
        } catch (MaximumWorldCountReachedException e) {
            logWorldError(Level.FINE, e);
            return SafeResult.error("Trop de mondes utilisés (" + e.getMaximumWorldCount() + ").");
        } catch (CannotCloneWorldException e) {
            logWorldError(Level.SEVERE, e);
            return SafeResult.error("Impossible de cloner le monde \"" + e.getWorldName() + "\".");
        } catch (CannotCreateLobbyException e) {
            logWorldError(Level.WARNING, e);
            return SafeResult.error(e.getMessage());
        }
    }

    private void logWorldError(Level level, Throwable throwable) {
        logger.log(level, "Couldn't create game: " + throwable.getMessage());
    }

    private Set<Player> findPresentPlayers(Set<Player> players) {
        return ongoingGames.stream().flatMap(x -> x.game().getPlayers().stream())
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
    public synchronized Optional<LGPlayerAndGame> getPlayerInGame(UUID playerUUID) {
        LGGameOrchestrator orchestrator = gamesByPlayerUUID.get(playerUUID);
        if (orchestrator == null) return Optional.empty();

        return orchestrator.game().getPlayer(playerUUID).map(p -> new LGPlayerAndGame(p, orchestrator));
    }

    @Override
    public synchronized Optional<LGGameOrchestrator> getGameById(String id) {
        return Optional.ofNullable(gamesById.get(id));
    }

    private synchronized void removeDeletedGame(LGGameOrchestrator orchestrator) {
        Preconditions.checkArgument(orchestrator.state() == LGGameState.DELETED, "The game must have been deleted.");
        ongoingGames.remove(orchestrator);
        gamesById.remove(orchestrator.game().getId());
    }
}
