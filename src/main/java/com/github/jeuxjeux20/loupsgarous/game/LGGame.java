package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface LGGame {
    ImmutableSet<LGPlayer> getPlayers();

    ImmutableMap<UUID, LGPlayer> getPlayerByUUIDMap();

    default Optional<? extends LGPlayer> getPlayer(LGPlayer player) {
        return getPlayer(player.getPlayerUUID());
    }

    default Optional<? extends LGPlayer> getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    Optional<? extends LGPlayer> getPlayer(UUID playerUUID);

    LGGameTurn getTurn();

    default Stream<LGPlayer> getAlivePlayers() {
        return getPlayers().stream().filter(LGPlayer::isAlive);
    }

    default Stream<LGPlayer> getPresentPlayers() {
        return getPlayers().stream().filter(LGPlayer::isPresent);
    }

    default boolean isEmpty() {
        return !getPresentPlayers().findAny().isPresent();
    }

    default Optional<LGPlayer> findByName(String name) {
        return getPlayers().stream().filter(x -> x.getName().equals(name)).findAny();
    }
}
