package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface LGGame {
    ImmutableSet<LGPlayer> getPlayers();

    ImmutableMap<UUID, LGPlayer> getPlayerByUUIDMap();

    Optional<LGPlayer> getPlayerByUUID(UUID playerUUID);

    LGGameTurn getTurn();

    default Stream<LGPlayer> getAlivePlayers() {
        return getPlayers().stream().filter(LGPlayer::isAlive);
    }

    default Optional<LGPlayer> findByName(String name) {
        return getPlayers().stream().filter(x -> x.getName().equals(name)).findAny();
    }
}
