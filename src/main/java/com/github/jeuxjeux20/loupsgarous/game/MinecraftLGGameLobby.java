package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.IllegalPlayerCountException;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.SnapshotComposition;
import com.github.jeuxjeux20.loupsgarous.game.events.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.Events;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class MinecraftLGGameLobby implements LGGameLobby {
    private final LGGameOrchestrator orchestrator;
    private final Set<Player> players;
    private final MutableComposition composition;
    private Player owner;

    public MinecraftLGGameLobby(LGGameLobbyInfo lobbyInfo, LGGameOrchestrator orchestrator) {
        this.players = new HashSet<>(lobbyInfo.getPlayers());
        this.owner = lobbyInfo.getOwner();
        this.composition = new LobbyComposition(lobbyInfo.getComposition());

        Preconditions.checkArgument(players.contains(owner), "The owner is not in the players collection.");
        Preconditions.checkArgument(players.size() <= composition.getPlayerCount(),
                "There are more players than the given composition is supposed to have.");

        this.orchestrator = orchestrator;

        registerPlayerQuitEvents();
    }

    @Override
    public boolean canAddPlayer() {
        return composition.getPlayerCount() != players.size() && !isLocked();
    }

    @Override
    public boolean canRemovePlayer() {
        return !isLocked();
    }

    @Override
    public boolean addPlayer(Player player) {
        if (!canAddPlayer()) return false;

        boolean added = players.add(player);
        if (added) {
            orchestrator.callEvent(new LGLobbyPlayerJoinEvent(orchestrator, player));
        }
        return added;
    }

    @Override
    public boolean removePlayer(Player player) {
        if (!canRemovePlayer()) return false;

        boolean removed = players.remove(player);
        if (removed) {
            orchestrator.callEvent(new LGLobbyPlayerQuitEvent(orchestrator, player));

            if (!players.isEmpty()) {
                if (player == owner) {
                    owner = players.iterator().next();
                }
            }
        }
        return removed;
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
    public ImmutableSet<Player> getPlayers() {
        return ImmutableSet.copyOf(players);
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
        Preconditions.checkArgument(players.contains(owner), "The given owner isn't present in the lobby.");
        if (owner == this.owner) return;

        Player oldOwner = this.owner;
        this.owner = owner;

        orchestrator.callEvent(new LGLobbyOwnerChangeEvent(orchestrator, oldOwner, owner));
    }

    private void registerPlayerQuitEvents() {
        Events.merge(PlayerEvent.class, PlayerQuitEvent.class, PlayerKickEvent.class)
                .expireIf(e -> isLocked())
                .handler(e -> removePlayer(e.getPlayer()));

        Events.subscribe(PlayerChangedWorldEvent.class)
                .expireIf(e -> isLocked())
                .filter(e -> e.getFrom() == orchestrator.getWorld().getCBWorld())
                .handler(e -> removePlayer(e.getPlayer()));
    }

    private final class LobbyComposition extends MutableComposition {
        public LobbyComposition(Composition composition) {
            super(composition);
        }

        @Override
        protected void checkPlayerCount(int playerCount) throws IllegalPlayerCountException {
            super.checkPlayerCount(playerCount);

            if (MinecraftLGGameLobby.this.players.size() > playerCount) {
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
