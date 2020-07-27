package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.metadata.MetadataMap;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Holds data of a loups-garous game.
 */
public interface LGGame {
    String getId();

    LGGameState getState();

    ImmutableSet<LGPlayer> getPlayers();

    LGGameTurn getTurn();

    @Nullable LGEnding getEnding();

    @Nullable LGPlayer getOwner();

    MetadataMap getMetadata();


    Optional<? extends LGPlayer> getPlayer(UUID playerUUID);

    /**
     * Gets a player by the given UUID or throw a {@link PlayerAbsentException}.
     *
     * @param playerUUID the player's UUID
     * @throws PlayerAbsentException when the player has not been found
     * @return the found player
     */
    LGPlayer getPlayerOrThrow(UUID playerUUID);

    /**
     * Throws a {@link PlayerAbsentException} when the given player
     * isn't present in the game.
     *
     * @param player the player
     * @throws PlayerAbsentException when the player has not been found
     * @return the same player
     */
    LGPlayer ensurePresent(LGPlayer player);


    default Stream<LGPlayer> getAlivePlayers() {
        return getPlayers().stream().filter(LGPlayer::isAlive);
    }

    default boolean isEmpty() {
        for (LGPlayer player : getPlayers()) {
            if (player.isPresent()) {
                return false;
            }
        }
        return true;
    }

    default Optional<LGPlayer> findByName(String name) {
        return getPlayers().stream().filter(x -> x.getName().equals(name)).findAny();
    }
}
