package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.IllegalPlayerCountException;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.SnapshotComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.gui.CompositionGui;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.lobby.LGLobbyCompositionChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.lobby.LGLobbyOwnerChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerQuitEvent;
import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import me.lucko.helper.Events;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;

class MinecraftLGGameLobby implements LGGameLobby {
    private final MutableLGGameOrchestrator orchestrator;
    private final MutableComposition composition;
    private final LobbyTeleporter lobbyTeleporter;
    private Player owner;
    private final LGGameManager gameManager;
    private final CompositionValidator compositionValidator;
    private @Nullable CompositionValidator.Problem.Type worseCompositionProblemType;
    private final CompositionGui.Factory compositionGuiFactory;

    @Inject
    MinecraftLGGameLobby(@Assisted LGGameLobbyInfo lobbyInfo,
                         @Assisted MutableLGGameOrchestrator orchestrator,
                         LobbyTeleporter.Factory lobbyTeleporterFactory,
                         LGGameManager gameManager,
                         CompositionValidator compositionValidator,
                         CompositionGui.Factory compositionGuiFactory) throws CannotCreateLobbyException {

        Preconditions.checkArgument(lobbyInfo.getPlayers().size() <= lobbyInfo.getComposition().getPlayerCount(),
                "There are more players than the given composition is supposed to have.");

        this.gameManager = gameManager;
        this.orchestrator = orchestrator;

        this.owner = lobbyInfo.getOwner();
        this.composition = new LobbyComposition(lobbyInfo.getComposition());
        this.lobbyTeleporter = lobbyTeleporterFactory.create();
        this.compositionValidator = compositionValidator;
        this.compositionGuiFactory = compositionGuiFactory;

        lobbyTeleporter.bindWith(orchestrator);

        registerPlayerQuitEvents();
        updateCompositionProblemType();
    }

    private boolean canAddPlayer() {
        return composition.getPlayerCount() != getGame().getPlayers().size() && !isLocked();
    }

    private boolean canRemovePlayer(UUID playerUUID) {
        return !isLocked() || getGame().getPlayer(playerUUID).map(LGPlayer::isPresent).orElse(false);
    }

    @Override
    public World getWorld() {
        return lobbyTeleporter.getWorld();
    }

    private boolean canPlayerJoin(Player player) {
        // The LGGameManager approach works well for now
        // but it will cause issues with BungeeCord support.

        return player.isOnline() &&
               player.hasPermission("loupsgarous.game.join") &&
               canAddPlayer() &&
               !gameManager.getPlayerInGame(player).isPresent();
    }

    @Override
    public boolean addPlayer(Player player) {
        if (!canPlayerJoin(player)) {
            return false;
        }

        MutableLGPlayer lgPlayer = new MutableLGPlayer(player);

        boolean added = getGame().addPlayerIfAbsent(lgPlayer) == null;
        if (added) {
            orchestrator.callEvent(new LGPlayerJoinEvent(orchestrator, player, lgPlayer));

            lobbyTeleporter.teleportPlayerIn(player);
        }
        return added;
    }

    @Override
    public boolean removePlayer(UUID playerUUID) {
        if (!canRemovePlayer(playerUUID)) return false;

        @Nullable MutableLGPlayer lgPlayer = getGame().getPlayer(playerUUID).orElse(null);
        if (lgPlayer == null) return false;

        if (orchestrator.getState().wentThrough(LGGameState.STARTED)) {
            lgPlayer.setAway(true);
        } else {
            getGame().removePlayer(playerUUID);
        }

        orchestrator.callEvent(new LGPlayerQuitEvent(orchestrator, playerUUID, lgPlayer));

        Player onlinePlayer = lgPlayer.getOfflineMinecraftPlayer().getPlayer();
        if (onlinePlayer != null) {
            lobbyTeleporter.teleportPlayerOut(onlinePlayer);
        }

        if (playerUUID.equals(owner.getUniqueId()) && orchestrator.getState().isEnabled()) {
            putRandomOwner();
        }

        return true;
    }

    private void putRandomOwner() {
        if (getGame().isEmpty()) return;

        setOwner(getGame().getPresentPlayers().findAny()
                .flatMap(LGPlayer::getMinecraftPlayer)
                .orElseThrow(() -> new AssertionError("Wait what, how is the owner I got offline?")));
    }

    @Override
    public void openOwnerGui() {
        if (isLocked()) return;

        CompositionGui gui = compositionGuiFactory.create(owner, composition);
        gui.open();

        Events.merge(LGEvent.class,
                LGGameStartEvent.class, LGGameDeletedEvent.class, LGLobbyOwnerChangeEvent.class)
                .expireIf(x -> !gui.isValid())
                .filter(x -> x.getOrchestrator() == orchestrator)
                .handler(e -> gui.close())
                .bindWith(orchestrator);
    }

    @Override
    public Composition getComposition() {
        return new SnapshotComposition(composition);
    }

    @Override
    public @Nullable CompositionValidator.Problem.Type getWorstCompositionProblemType() {
        return worseCompositionProblemType;
    }

    private void updateCompositionProblemType() {
        worseCompositionProblemType = compositionValidator.validate(composition).stream()
                .map(CompositionValidator.Problem::getType)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    @Override
    public LGGameOrchestrator gameOrchestrator() {
        return orchestrator;
    }

    @Override
    public boolean isLocked() {
        return orchestrator.getState() != LGGameState.WAITING_FOR_PLAYERS &&
               orchestrator.getState() != LGGameState.READY_TO_START;
    }

    @Override
    public Player getOwner() {
        return owner;
    }

    @Override
    public void setOwner(Player owner) {
        Preconditions.checkArgument(getGame().getPlayer(owner.getUniqueId()).isPresent(),
                "The given owner isn't present in the lobby.");

        if (owner == this.owner) return;

        Player oldOwner = this.owner;
        this.owner = owner;

        orchestrator.callEvent(new LGLobbyOwnerChangeEvent(orchestrator, oldOwner, owner));
    }

    private void registerPlayerQuitEvents() {
        Events.merge(PlayerEvent.class, PlayerQuitEvent.class, PlayerKickEvent.class)
                .expireIf(e -> orchestrator.getState().isDisabled())
                .handler(e -> removePlayer(e.getPlayer()))
                .bindWith(orchestrator);

        Events.subscribe(PlayerChangedWorldEvent.class)
                .expireIf(e -> orchestrator.getState().isDisabled())
                .filter(e -> e.getFrom() == orchestrator.getWorld())
                .handler(e -> removePlayer(e.getPlayer()))
                .bindWith(orchestrator);
    }

    private MutableLGGame getGame() {
        return orchestrator.getGame();
    }

    private final class LobbyComposition extends MutableComposition {
        public LobbyComposition(Composition composition) {
            super(composition);
        }

        @Override
        protected void checkPlayerCount(int playerCount) throws IllegalPlayerCountException {
            super.checkPlayerCount(playerCount);

            if (MinecraftLGGameLobby.this.getGame().getPlayers().size() > playerCount) {
                throw new IllegalPlayerCountException(getPlayerCount() - 1 == playerCount ?
                        "Impossible de retirer un joueur, car cela excluerait quelqu'un." :
                        "Impossible de retirer autant de joueurs, car cela excluerait quelqu'un.");
            }
        }

        @Override
        protected void onChange() {
            updateCompositionProblemType();
            orchestrator.callEvent(new LGLobbyCompositionChangeEvent(orchestrator));
        }
    }
}
