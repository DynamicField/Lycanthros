package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.SnapshotComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.gui.CompositionGui;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator;
import com.github.jeuxjeux20.loupsgarous.game.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyCompositionChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.lobby.LGLobbyOwnerChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.event.player.LGPlayerQuitEvent;
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
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.UUID;

class MinecraftLGLobby implements LGLobby {
    private final MutableLGGameOrchestrator orchestrator;
    private final MutableComposition composition;
    private final LobbyTeleporter lobbyTeleporter;
    private @MonotonicNonNull Player owner;
    private final LGGameManager gameManager;
    private final CompositionValidator compositionValidator;
    private @Nullable CompositionValidator.Problem.Type worseCompositionProblemType;
    private final CompositionGui.Factory compositionGuiFactory;

    @Inject
    MinecraftLGLobby(@Assisted LGGameLobbyInfo lobbyInfo,
                     @Assisted MutableLGGameOrchestrator orchestrator,
                     LobbyTeleporter.Factory lobbyTeleporterFactory,
                     LGGameManager gameManager,
                     CompositionValidator compositionValidator,
                     CompositionGui.Factory compositionGuiFactory) throws CannotCreateLobbyException {
        this.gameManager = gameManager;
        this.orchestrator = orchestrator;

        this.composition = new LobbyComposition(lobbyInfo.getComposition());
        this.lobbyTeleporter = lobbyTeleporterFactory.create();
        this.compositionValidator = compositionValidator;
        this.compositionGuiFactory = compositionGuiFactory;

        lobbyTeleporter.bindWith(orchestrator);

        registerPlayerQuitEvents();
        updateCompositionProblemType();
    }

    private boolean canAddPlayer() {
        return getSlotsTaken() < getTotalSlotCount() && !isLocked();
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
            if (owner == null) {
                owner = player;
            }

            Events.call(new LGPlayerJoinEvent(orchestrator, player, lgPlayer));

            lobbyTeleporter.teleportPlayerIn(player);
        }
        return added;
    }

    @Override
    public boolean removePlayer(UUID playerUUID) {
        MutableLGPlayer player = getGame().getPlayer(playerUUID).filter(LGPlayer::isPresent).orElse(null);
        if (player == null) return false;

        if (isLocked()) {
            player.setAway(true);
        } else {
            getGame().removePlayer(playerUUID);
        }

        Events.call(new LGPlayerQuitEvent(orchestrator, playerUUID, player));

        player.getMinecraftPlayerNoContext().ifPresent(lobbyTeleporter::teleportPlayerOut);

        if (playerUUID.equals(owner.getUniqueId()) && orchestrator.state().isEnabled()) {
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
                .bindWith(gui);
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
        return orchestrator.state() != LGGameState.WAITING_FOR_PLAYERS &&
               orchestrator.state() != LGGameState.READY_TO_START;
    }

    @Override
    public Player getOwner() {
        Preconditions.checkState(owner != null, "There is no owner.");
        return owner;
    }

    @Override
    public void setOwner(Player owner) {
        Preconditions.checkArgument(getGame().getPlayer(owner.getUniqueId()).isPresent(),
                "The given owner isn't present in the lobby.");

        if (owner == this.owner) return;

        Player oldOwner = this.owner;
        this.owner = owner;

        Events.call(new LGLobbyOwnerChangeEvent(orchestrator, oldOwner, owner));
    }

    private void registerPlayerQuitEvents() {
        Events.merge(PlayerEvent.class, PlayerQuitEvent.class, PlayerKickEvent.class)
                .expireIf(e -> orchestrator.state().isDisabled())
                .handler(e -> removePlayer(e.getPlayer()))
                .bindWith(orchestrator);

        Events.subscribe(PlayerChangedWorldEvent.class)
                .expireIf(e -> orchestrator.state().isDisabled())
                .filter(e -> e.getFrom() == orchestrator.world())
                .handler(e -> removePlayer(e.getPlayer()))
                .bindWith(orchestrator);
    }

    private MutableLGGame getGame() {
        return orchestrator.game();
    }

    private final class LobbyComposition extends MutableComposition {
        public LobbyComposition(Composition composition) {
            super(composition);
        }

        @Override
        public boolean isValidPlayerCount(int playerCount) {
            return super.isValidPlayerCount(playerCount) &&
                   getGame().getPlayers().size() <= playerCount;
        }

        @Override
        protected void onChange() {
            updateCompositionProblemType();
            Events.call(new LGLobbyCompositionChangeEvent(orchestrator));
        }
    }
}
