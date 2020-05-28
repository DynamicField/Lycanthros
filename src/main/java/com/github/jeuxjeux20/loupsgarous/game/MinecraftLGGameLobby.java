package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.IllegalPlayerCountException;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.SnapshotComposition;
import com.github.jeuxjeux20.loupsgarous.game.events.lobby.LGLobbyCompositionChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.lobby.LGLobbyOwnerChangeEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerJoinEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.player.LGPlayerQuitEvent;
import com.google.common.base.Preconditions;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;
import java.util.UUID;

class MinecraftLGGameLobby implements LGGameLobby {
    private final MinecraftLGGameOrchestrator orchestrator;
    private final MutableComposition composition;
    private Player owner;

    public MinecraftLGGameLobby(LGGameLobbyInfo lobbyInfo, MinecraftLGGameOrchestrator orchestrator) {
        this.orchestrator = orchestrator;

        this.owner = lobbyInfo.getOwner();
        this.composition = new LobbyComposition(lobbyInfo.getComposition());

        Preconditions.checkArgument(lobbyInfo.getPlayers().size() <= composition.getPlayerCount(),
                "There are more players than the given composition is supposed to have.");

        registerPlayerQuitEvents();
    }

    private boolean canAddPlayer() {
        return composition.getPlayerCount() != getGame().getPlayers().size() && !isLocked();
    }

    private boolean canRemovePlayer(UUID playerUUID) {
        return !isLocked() || getGame().getPlayer(playerUUID).map(LGPlayer::isPresent).orElse(false);
    }

    @Override
    public boolean addPlayer(Player player) {
        if (!player.isOnline() || !canAddPlayer()) return false;

        MutableLGPlayer lgPlayer = new MutableLGPlayer(player);

        boolean added = getGame().addPlayerIfAbsent(lgPlayer) == null;
        if (added) {
            orchestrator.callEvent(new LGPlayerJoinEvent(orchestrator, player, lgPlayer));
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
    public Optional<MutableComposition> getMutableComposition() {
        if (isLocked()) return Optional.empty();
        return Optional.of(composition);
    }

    @Override
    public Composition getComposition() {
        return new SnapshotComposition(composition);
    }

    @Override
    public MinecraftLGGameOrchestrator getOrchestrator() {
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
                .filter(e -> e.getFrom() == orchestrator.getWorld().getCBWorld())
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
            orchestrator.callEvent(new LGLobbyCompositionChangeEvent(orchestrator));
        }
    }
}
