package com.github.jeuxjeux20.loupsgarous.lobby;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.event.LGGameInitializedEvent;
import com.github.jeuxjeux20.loupsgarous.event.lobby.LGLobbyOwnerChangeEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.event.player.LGPlayerQuitEvent;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

class MinecraftLGLobby implements LGLobby {
    private final InternalLGGameOrchestrator orchestrator;
    private final LobbyTeleporter lobbyTeleporter;
    private final LGGameManager gameManager;
    private final LGLobbyCompositionManager compositionManager;

    @Inject
    MinecraftLGLobby(@Assisted LGGameBootstrapData bootstrapData,
                     @Assisted InternalLGGameOrchestrator orchestrator,
                     LobbyTeleporter.Factory lobbyTeleporterFactory,
                     LGGameManager gameManager,
                     LGLobbyCompositionManager.Factory compositionManagerFactory) throws LobbyCreationException {
        this.gameManager = gameManager;
        this.orchestrator = orchestrator;
        this.lobbyTeleporter = lobbyTeleporterFactory.create();
        this.compositionManager = compositionManagerFactory.create(orchestrator, bootstrapData);

        lobbyTeleporter.bindWith(orchestrator);

        try {
            getGame().setOwner(addPlayer(bootstrapData.getOwner()));
        } catch (PlayerJoinException e) {
            throw new InvalidOwnerException(e);
        }

        registerPlayerQuitEvents();
    }

    @Override
    public World getWorld() {
        return lobbyTeleporter.getWorld();
    }

    private void checkPlayer(Player player) throws PlayerJoinException {
        // The LGGameManager approach works well for now
        // but it will cause issues with BungeeCord support.

        if (!player.isOnline()) {
            throw new PlayerOfflineException(player);
        }

        String permission = "loupsgarous.game.join";
        if (!player.hasPermission(permission)) {
            throw new PermissionMissingException(permission, player);
        }

        if (isLocked()) {
            throw InaccessibleLobbyException.lobbyLocked();
        } else if (getSlotsTaken() == getTotalSlotCount()) {
            throw InaccessibleLobbyException.lobbyFull();
        }

        if (gameManager.getPlayerInGame(player).isPresent()) {
            throw new PlayerAlreadyInGameException(player);
        }
    }

    @Override
    public LGPlayer addPlayer(Player player) throws PlayerJoinException {
        checkPlayer(player);

        OrchestratedLGPlayer lgPlayer = new OrchestratedLGPlayer(
                new BackingLGPlayer(player), orchestrator
        );
        getGame().addPlayer(lgPlayer);

        if (orchestrator.state() != LGGameState.UNINITIALIZED) {
            onPlayerAdd(player, lgPlayer);
        } else {
            // Wait until the game initializes so we can do the usual stuff.
            // -> We don't TP the player until the game is initialized.
            Events.subscribe(LGGameInitializedEvent.class)
                    .filter(orchestrator::isMyEvent)
                    .expireAfter(1)
                    .handler(e -> onPlayerAdd(player, lgPlayer));
        }

        return lgPlayer;
    }

    private void onPlayerAdd(Player player, LGPlayer lgPlayer) {
        Events.call(new LGPlayerJoinEvent(orchestrator, player, lgPlayer));
        lobbyTeleporter.teleportPlayerIn(player);
    }

    @Override
    public boolean removePlayer(UUID playerUUID) {
        OrchestratedLGPlayer player = getGame().getPlayer(playerUUID)
                .filter(LGPlayer::isPresent)
                .orElse(null);
        if (player == null) return false;

        player.goAway();
        if (!isLocked()) {
            // Let's not remove the player when the game's locked.
            // This would lead to weird reference issues.
            getGame().removePlayer(playerUUID);
        }

        if (getGame().getOwner() == null) {
            putRandomOwner();
        }

        player.minecraftNoContext(lobbyTeleporter::teleportPlayerOut);

        Events.call(new LGPlayerQuitEvent(orchestrator, playerUUID, player));
        
        return true;
    }

    private void putRandomOwner() {
        if (!getGame().isEmpty()) {
            setOwner(getGame().getPlayers().iterator().next());
        }
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }

    @Override
    public boolean isLocked() {
        return orchestrator.state() != LGGameState.UNINITIALIZED &&
               orchestrator.state() != LGGameState.WAITING_FOR_PLAYERS &&
               orchestrator.state() != LGGameState.READY_TO_START;
    }

    @Override
    public LGPlayer getOwner() {
        LGPlayer owner = getGame().getOwner();
        return owner == null ? LGPlayer.NULL : owner;
    }

    @Override
    public void setOwner(LGPlayer owner) {
        LGPlayer newOwner = getGame().ensurePresent(owner);

        if (owner == getGame().getOwner()) return;

        getGame().setOwner(newOwner);

        Events.call(new LGLobbyOwnerChangeEvent(orchestrator, owner));
    }

    @Override
    public LGLobbyCompositionManager composition() {
        return compositionManager;
    }

    private void registerPlayerQuitEvents() {
        Events.merge(PlayerEvent.class, PlayerQuitEvent.class, PlayerKickEvent.class)
                .expireIf(e -> orchestrator.state().isDisabled())
                .handler(e -> removePlayer(e.getPlayer()))
                .bindWith(orchestrator);

        Events.subscribe(PlayerChangedWorldEvent.class)
                .expireIf(e -> orchestrator.state().isDisabled())
                .filter(e -> e.getFrom() == getWorld())
                .handler(e -> removePlayer(e.getPlayer()))
                .bindWith(orchestrator);
    }

    private OrchestratedLGGame getGame() {
        return orchestrator.game();
    }
}
