package com.github.jeuxjeux20.loupsgarous.game.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.LGKillCause;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;

/**
 * Stores pending kills and allows for differed killing.
 * <p>
 * This is typically used at night and kills are revealed just after the village wakes up.
 */
public interface PendingKillRegistry {
    /**
     * Gets all the pending kills.
     *
     * @return the pending kills
     */
    ImmutableSet<LGKill> getAll();

    /**
     * Gets the cause of the given player's future death, if they will die.
     *
     * @param player the player, probably a victim
     * @return an optional containing the cause
     */
    Optional<LGKillCause> get(LGPlayer player);

    /**
     * Adds a pending kill with the given victim and cause. Any pending
     * kill with the same victim will be replaced by the given one.
     *
     * @param victim the victim to kill
     * @param cause  the cause of the victim's death
     */
    void add(LGPlayer victim, LGKillCause cause);

    /**
     * Adds the given kill to the pending kills. Any pending
     * kill with the same player will be replaced by the given one.
     *
     * @param kill the kill to add
     */
    default void add(LGKill kill) {
        add(kill.getVictim(), kill.getCause());
    }

    /**
     * Removes the pending kill of the given player.
     *
     * @param player the player
     * @return {@code true} if this player was going to die, {@code false} if not
     */
    boolean remove(LGPlayer player);

    /**
     * Returns {@code true} if the given player is a victim in a pending kill.
     *
     * @param player the player
     * @return {@code true} if the player is going to die, {@code false} if not
     */
    boolean contains(LGPlayer player);

    /**
     * Returns {@code true} if there are no pending kills.
     *
     * @return @code true} if there are no pending kills, {@code false} if not
     */
    boolean isEmpty();

    /**
     * Kills victims of every pending kill with an alive victim.
     *
     * @see LGKillEvent
     */
    void reveal();
}
