package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.SnapshotComposition;
import com.google.common.collect.ImmutableSet;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A set of data to create a new Loups-Garous game instance.
 */
public final class LGGameLobbyInfo {
    private final Player owner;
    private final String id;
    private final ImmutableSet<Player> players;
    private final Composition composition;

    public LGGameLobbyInfo(Set<Player> players, Composition composition,
                           @Nullable Player owner, String id) {
        ImmutableSet<Player> onlinePlayers
                = players.stream().filter(OfflinePlayer::isOnline).collect(ImmutableSet.toImmutableSet());

        if (onlinePlayers.isEmpty()) {
            throw new IllegalArgumentException("There are no online players.");
        }
        if (onlinePlayers.size() > composition.getCards().size()) {
            throw new IllegalArgumentException("There are more players than cards.");
        }
        if (owner != null && !onlinePlayers.contains(owner)) {
            throw new IllegalArgumentException("The owner is not online and/or present in the given players.");
        }

        this.players = onlinePlayers;
        this.composition = new SnapshotComposition(composition);
        this.id = id;

        this.owner = determineOwner(owner);
    }

    private Player determineOwner(@Nullable Player presetOwner) {
        if (players.contains(presetOwner)) {
            return presetOwner;
        }
        return players.iterator().next();
    }


    public String getId() {
        return id;
    }

    public ImmutableSet<Player> getPlayers() {
        return players;
    }

    public Composition getComposition() {
        return composition;
    }

    public Player getOwner() {
        return owner;
    }
}
