package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameInitializeEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyOwnerChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerQuitEvent;
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
    private final MutableLGGameOrchestrator orchestrator;
    private final LobbyTeleporter lobbyTeleporter;
    private final LGGameManager gameManager;
    private final LGLobbyCompositionManager compositionManager;

    private MutableLGPlayer owner;

    @Inject
    MinecraftLGLobby(@Assisted LGGameBootstrapData bootstrapData,
                     @Assisted MutableLGGameOrchestrator orchestrator,
                     LobbyTeleporter.Factory lobbyTeleporterFactory,
                     LGGameManager gameManager,
                     LGLobbyCompositionManager.Factory compositionManagerFactory) throws LobbyCreationException {
        this.gameManager = gameManager;
        this.orchestrator = orchestrator;
        this.lobbyTeleporter = lobbyTeleporterFactory.create();
        this.compositionManager = compositionManagerFactory.create(orchestrator, bootstrapData);

        lobbyTeleporter.bindWith(orchestrator);

        try {
            this.owner = addPlayer(bootstrapData.getOwner());
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
            throw new PlayerAlreadyPresentException(player);
        }
    }

    @Override
    public MutableLGPlayer addPlayer(Player player) throws PlayerJoinException {
        checkPlayer(player);

        MutableLGPlayer lgPlayer = new MutableLGPlayer(player);
        getGame().addPlayer(lgPlayer);

        if (orchestrator.state() != LGGameState.UNINITIALIZED) {
            onPlayerAdd(player, lgPlayer);
        } else {
            // Wait until the game initializes so we can do the usual stuff.
            // -> We don't TP the player until the game is initialized.
            Events.subscribe(LGGameInitializeEvent.class)
                    .filter(orchestrator::isMyEvent)
                    .expireAfter(1)
                    .handler(e -> onPlayerAdd(player, lgPlayer));
        }

        return lgPlayer;
    }

    private void onPlayerAdd(Player player, MutableLGPlayer lgPlayer) {
        Events.call(new LGPlayerJoinEvent(orchestrator, player, lgPlayer));
        lobbyTeleporter.teleportPlayerIn(player);
    }

    @Override
    public boolean removePlayer(UUID playerUUID) {
        MutableLGPlayer player = getGame().getPlayer(playerUUID).filter(LGPlayer::isPresent).orElse(null);
        if (player == null) return false;

        player.setAway(true);
        if (!isLocked()) {
            // Let's not remove the player when the game's locked.
            // This would lead to weird reference issues.
            getGame().removePlayer(playerUUID);
        }

        if (player == owner) {
            putRandomOwner();
        }

        player.getMinecraftPlayerNoContext().ifPresent(lobbyTeleporter::teleportPlayerOut);

        Events.call(new LGPlayerQuitEvent(orchestrator, playerUUID, player));

        return true;
    }

    private void putRandomOwner() {
        if (!getGame().isEmpty()) {
            setOwner(getGame().getPlayers().iterator().next());
        } else {
            // SPECIAL CASE: Here the game is empty, however, we don't want the owner to be null.
            // So, let's just make the current owner away.
            owner.setAway(true);
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
        return owner;
    }

    @Override
    public void setOwner(LGPlayer owner) {
        MutableLGPlayer newOwner = getGame().getPlayer(owner.getPlayerUUID())
                .orElseThrow(() -> new IllegalArgumentException("The given owner isn't present in the lobby."));

        if (owner == this.owner) return;

        this.owner = newOwner;

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

    private MutableLGGame getGame() {
        return orchestrator.game();
    }
}
