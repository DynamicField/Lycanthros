package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface LGPlayer {
    /**
     * Gets the minecraft player this player is linked to.
     *
     * @return the player
     */
    default Optional<Player> getMinecraftPlayer() {
        if (isAway()) return Optional.empty();
        return Optional.ofNullable(Bukkit.getPlayer(getPlayerUUID()));
    }

    UUID getPlayerUUID();

    LGCard getCard();

    boolean isDead();

    default boolean isAlive() {
        return !isDead();
    }

    default String getName() {
        return getMinecraftPlayer().map(HumanEntity::getName).orElse("[Inconnu]");
    }

    boolean isAway();
}
