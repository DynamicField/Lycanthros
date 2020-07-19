package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTag;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.metadata.MetadataMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public interface LGPlayer extends UserFriendlyNamed {
    LGPlayer NULL = Null.INSTANCE;

    UUID getPlayerUUID();

    LGCard getCard();

    boolean isDead();

    boolean isAway();

    ImmutableSet<LGTag> getTags();

    ImmutableClassToInstanceMap<LGPower> getPowers();

    default boolean hasPower(Class<? extends LGPower> power) {
        return getPowers().containsKey(power);
    }

    default <T extends LGPower> Optional<T> getPower(Class<T> powerClass) {
        return Optional.ofNullable(getPowers().getInstance(powerClass));
    }

    default <T extends LGPower> T getPowerOrThrow(Class<T> powerClass) {
        T power = getPowers().getInstance(powerClass);

        if (power == null) {
            throw new NoSuchElementException("No power with class " + powerClass + " found.");
        }

        return power;
    }

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

    @Override
    default String getUserFriendlyName() {
        return getName();
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

    class Null implements LGPlayer {
        public static final Null INSTANCE = new Null();

        private final MetadataMap metadataMap = MetadataMap.create();

        private Null() {
        }

        @Override
        public UUID getPlayerUUID() {
            return new UUID(0, 0);
        }

        @Override
        public LGCard getCard() {
            return new LGCard.Unknown();
        }

        @Override
        public boolean isDead() {
            return false;
        }

        @Override
        public boolean isAway() {
            return true;
        }

        @Override
        public ImmutableSet<LGTag> getTags() {
            return ImmutableSet.of();
        }

        @Override
        public ImmutableClassToInstanceMap<LGPower> getPowers() {
            return ImmutableClassToInstanceMap.of();
        }

        @Override
        public MetadataMap metadata() {
            return metadataMap;
        }
    }
}
