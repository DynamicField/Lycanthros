package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTag;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.metadata.MetadataMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface LGPlayer {
    UUID getPlayerUUID();

    LGCard getCard();

    boolean isDead();

    boolean isAway();

    ImmutableSet<LGTag> getTags();

    MetadataMap metadata();

    default boolean isAlive() {
        return !isDead();
    }

    default boolean isPresent() {
        return !isAway();
    }

    default String getName() {
        String name = getOfflineMinecraftPlayer().getName();
        return name == null ? "[Inconnu]" : name;
    }

    /**
     * Gets the minecraft player this player is linked to, if the player is away,
     * this returns {@link Optional#empty()}.
     *
     * @return the player
     */
    default Optional<Player> getMinecraftPlayer() {
        if (isAway()) return Optional.empty();
        return Optional.ofNullable(Bukkit.getPlayer(getPlayerUUID()));
    }

    /**
     * Gets the minecraft player this player is linked to, without taking account
     * of the context (e.g. the player is away).
     *
     * @return the player
     */
    default Optional<Player> getMinecraftPlayerNoContext() {
        return Optional.ofNullable(Bukkit.getPlayer(getPlayerUUID()));
    }

    /**
     * Gets the minecraft player this player is linked to, even if it is offline.
     *
     * @return the player, offline or not
     * @implSpec The default implementation uses {@link Bukkit#getOfflinePlayer(UUID)}.
     */
    default OfflinePlayer getOfflineMinecraftPlayer() {
        return Bukkit.getOfflinePlayer(getPlayerUUID());
    }
}
