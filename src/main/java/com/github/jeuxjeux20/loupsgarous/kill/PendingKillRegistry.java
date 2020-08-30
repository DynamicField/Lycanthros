package com.github.jeuxjeux20.loupsgarous.kill;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.event.LGKillEvent;
import com.github.jeuxjeux20.loupsgarous.kill.causes.LGKillCause;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.STARTED;

public class PendingKillRegistry {
    private final LGGameOrchestrator orchestrator;
    private final PlayerKiller playerKiller;

    private final Map<LGPlayer, LGKillCause> kills = new HashMap<>();

    @Inject
    PendingKillRegistry(LGGameOrchestrator orchestrator, PlayerKiller playerKiller) {
        this.orchestrator = orchestrator;
        this.playerKiller = playerKiller;
    }

    /**
     * Gets all the pending kills.
     *
     * @return the pending kills
     */
    public ImmutableSet<LGKill> getAll() {
        return kills.entrySet().stream()
                .map(e -> LGKill.of(e.getKey(), e.getValue()))
                .collect(ImmutableSet.toImmutableSet());
    }

    /**
     * Gets the cause of the given player's future death, if they will die.
     *
     * @param player the player, probably a victim
     * @return an optional containing the cause
     */
    public Optional<LGKillCause> get(LGPlayer player) {
        return Optional.ofNullable(kills.get(player));
    }

    /**
     * Adds a pending kill with the given victim and cause. Any pending kill with the same victim
     * will be replaced by the given one.
     *
     * @param victim the victim to kill
     * @param cause  the cause of the victim's death
     */
    public void add(LGPlayer victim, LGKillCause cause) {
        kills.put(victim, cause);
    }

    /**
     * Removes the pending kill of the given player.
     *
     * @param player the player
     * @return {@code true} if this player was going to die, {@code false} if not
     */
    public boolean remove(LGPlayer player) {
        return kills.remove(player) != null;
    }

    /**
     * Returns {@code true} if the given player is a victim in a pending kill.
     *
     * @param player the player
     * @return {@code true} if the player is going to die, {@code false} if not
     */
    public boolean contains(LGPlayer player) {
        return kills.containsKey(player);
    }

    /**
     * Returns {@code true} if there are no pending kills.
     *
     * @return @code true} if there are no pending kills, {@code false} if not
     */
    public boolean isEmpty() {
        return kills.isEmpty();
    }

    /**
     * Kills victims of every pending kill with an alive victim.
     *
     * @see LGKillEvent
     */
    public void reveal() {
        orchestrator.getState().mustBe(STARTED);

        ImmutableSet<LGKill> kills = getAll();

        List<LGKill> applicableKills = kills.stream()
                .filter(LGKill::canTakeEffect)
                .collect(Collectors.toList());

        playerKiller.applyKills(applicableKills);
    }

    /**
     * Adds the given kill to the pending kills. Any pending kill with the same player will be
     * replaced by the given one.
     *
     * @param kill the kill to add
     */
    public void add(LGKill kill) {
        add(kill.getVictim(), kill.getCause());
    }
}
