package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.cards.*;
import com.github.jeuxjeux20.loupsgarous.game.events.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.github.jeuxjeux20.loupsgarous.util.WordingUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Singleton
public class DefaultLGGameManager implements LGGameManager {
    private final MultiverseCore multiverse;
    private final LoupsGarous plugin;
    private final LGGameOrchestrator.Factory orchestratorFactory;
    private final CopyOnWriteArraySet<Player> playersLocked = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArrayList<LGGameOrchestrator> ongoingGames = new CopyOnWriteArrayList<>();

    @Inject
    public DefaultLGGameManager(MultiverseCore multiverse, LoupsGarous plugin,
                                LGGameOrchestrator.Factory orchestratorFactory) {
        this.multiverse = multiverse;
        this.plugin = plugin;
        this.orchestratorFactory = orchestratorFactory;

        plugin.getServer().getPluginManager().registerEvents(new RemoveGameOnDeleteListener(), plugin);
    }

    @Override
    public synchronized SafeResult<LGGameOrchestrator> startGame(String worldToClone, List<Player> players, CommandSender teleporter) {
        List<Player> playersLockedWhileStarting = playersLocked.stream().filter(players::contains).collect(Collectors.toList());
        if (!playersLockedWhileStarting.isEmpty()) {
            return SafeResult.error(playersLockedWhileStarting.size() > 1 ?
                    "Les joueurs " + WordingUtils.joiningCommaAnd(playersLocked.stream(), Player::getName) +
                    " sont en train d'accéder à une partie." :
                    "Le joueur " + playersLockedWhileStarting.get(0).getName() + " est en train d'accéder à une partie.");
        }

        playersLocked.addAll(players);
        try {
            List<Player> presentPlayers = ongoingGames.stream().flatMap(x -> x.getGame().getPlayers().stream())
                    .map(LGPlayer::getMinecraftPlayer)
                    .flatMap(OptionalUtils::stream)
                    .filter(players::contains)
                    .collect(Collectors.toList());

            int presentPlayersCount = presentPlayers.size();
            if (presentPlayersCount > 0) {
                return SafeResult.error(presentPlayersCount > 1 ?
                        "Les joueurs " + presentPlayers.stream().map(Player::getName).collect(Collectors.joining(", ")) +
                        " sont déjà en partie." :
                        "Le joueur " + presentPlayers.get(0).getName() + " est déjà en partie.");
            }

            UUID id = UUID.randomUUID();
            String gameWorldName = WORLD_PREFIX + id.toString();

            if (!multiverse.getMVWorldManager().cloneWorld(worldToClone, gameWorldName)) {
                String msg = "Impossible de cloner le monde.";
                plugin.getLogger().warning(msg);
                return SafeResult.error(msg);
            }

            MultiverseWorld mvWorld = multiverse.getMVWorldManager().getMVWorld(gameWorldName);
            mvWorld.setAlias(mvWorld.getName().substring(0, WORLD_PREFIX.length() + SHORT_ID_LENGTH));

            // Debug stuff

            players = Lists.newArrayList(players);
            while (players.size() < 7) {
                players.add(null);
            }

            LGCard[] lgCards = new LGCard[players.size()];
            Arrays.fill(lgCards, new VillageoisCard());
            lgCards[0] = new LoupGarouCard();
            lgCards[1] = new CupidonCard();
            lgCards[2] = new ChasseurCard();
            lgCards[3] = new VoyanteCard();
            lgCards[4] = new PetiteFilleCard();
            lgCards[5] = new VillageoisCard();
            lgCards[6] = new SorciereCard();

            // End debug stuff

            Set<UUID> playerUUIDs = players.stream()
                    .map(x -> x == null ? UUID.randomUUID() : x.getUniqueId())
                    .collect(Collectors.toSet());

            LGGameOrchestrator orchestrator = orchestratorFactory.create(
                    new LGGameLobbyInfo(playerUUIDs, Arrays.asList(lgCards), mvWorld, teleporter, id)
            );

            ongoingGames.add(orchestrator);

            orchestrator.teleportAllPlayers();

            return SafeResult.success(orchestrator);
        } finally {
            playersLocked.removeAll(players);
        }
    }

    @Override
    public synchronized final List<LGGameOrchestrator> getOngoingGames() {
        return Collections.unmodifiableList(Lists.newArrayList(ongoingGames));
    }

    private synchronized void removeDeletedGame(LGGameOrchestrator orchestrator) {
        Preconditions.checkArgument(orchestrator.getState() == LGGameState.DELETED, "The game must have been deleted.");
        ongoingGames.remove(orchestrator);
    }

    private class RemoveGameOnDeleteListener implements Listener {
        @EventHandler(priority = EventPriority.HIGH)
        public void onLGGameDeleted(LGGameDeletedEvent event) {
            removeDeletedGame(event.getOrchestrator());
        }
    }
}
