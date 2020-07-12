package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface LGGame {
    String getId();

    ImmutableSet<LGPlayer> getPlayers();

    LGGameTurn getTurn();

    Optional<LGEnding> getEnding();


    Optional<? extends LGPlayer> getPlayer(UUID playerUUID);

    default Optional<? extends LGPlayer> getPlayer(LGPlayer player) {
        return getPlayer(player.getPlayerUUID());
    }


    default Stream<LGPlayer> getAlivePlayers() {
        return getPlayers().stream().filter(LGPlayer::isAlive);
    }

    default boolean isEmpty() {
        for (LGPlayer player : getPlayers()) {
            if (player.isAway()) {
                return true;
            }
        }
        return false;
    }

    default Optional<LGPlayer> findByName(String name) {
        return getPlayers().stream().filter(x -> x.getName().equals(name)).findAny();
    }
}
