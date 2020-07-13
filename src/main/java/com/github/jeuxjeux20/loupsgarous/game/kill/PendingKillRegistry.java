package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.kill.reasons.LGKillReason;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;

/**
 * Stores pending kills.
 */
public interface PendingKillRegistry {
    ImmutableSet<LGKill> getAll();

    Optional<LGKillReason> get(LGPlayer player);

    void put(LGPlayer player, LGKillReason killReason);

    default void put(LGKill kill) {
        put(kill.getWhoDied(), kill.getReason());
    }

    boolean remove(LGPlayer player);

    boolean contains(LGPlayer player);

    boolean isEmpty();

    void reveal();
}
