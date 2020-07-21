package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.kill.causes.LGKillCause;
import com.github.jeuxjeux20.loupsgarous.game.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.game.powers.PowerRegistry;
import com.github.jeuxjeux20.loupsgarous.game.tags.LGTag;
import com.github.jeuxjeux20.loupsgarous.game.tags.TagRegistry;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.TeamRegistry;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.metadata.MetadataMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface LGPlayer extends UserFriendlyNamed {
    LGPlayer NULL = Null.INSTANCE;

    UUID getPlayerUUID();

    LGCard getCard();

    boolean isDead();

    default boolean isAlive() {
        return !isDead();
    }

    boolean isAway();

    default boolean isPresent() {
        return !isAway();
    }

    TeamRegistry teams();

    TagRegistry tags();

    PowerRegistry powers();

    MetadataMap metadata();

    void changeCard(LGCard newCard);

    boolean willDie();

    default boolean willNotDie() {
        return !willDie();
    }

    void die(LGKillCause cause);

    void dieLater(LGKillCause cause);

    void cancelFutureDeath();

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
        public TeamRegistry teams() {
            return new TeamRegistry() {
                @Override
                public ImmutableSet<LGTeam> get() {
                    return ImmutableSet.of();
                }

                @Override
                public boolean add(LGTeam item) {
                    return false;
                }

                @Override
                public boolean has(LGTeam item) {
                    return false;
                }

                @Override
                public boolean remove(LGTeam item) {
                    return false;
                }
            };
        }

        @Override
        public TagRegistry tags() {
            return new TagRegistry() {
                @Override
                public ImmutableSet<LGTag> get() {
                    return ImmutableSet.of();
                }

                @Override
                public boolean add(LGTag item) {
                    return false;
                }

                @Override
                public boolean has(LGTag item) {
                    return false;
                }

                @Override
                public boolean remove(LGTag item) {
                    return false;
                }
            };
        }

        @Override
        public PowerRegistry powers() {
           return new PowerRegistry() {
               @Override
               public ImmutableClassToInstanceMap<LGPower> get() {
                   return ImmutableClassToInstanceMap.of();
               }

               @Override
               public <T extends LGPower> Optional<T> get(Class<T> powerClass) {
                   return Optional.empty();
               }

               @Override
               public <T extends LGPower> T getOrThrow(Class<T> powerClass) {
                   return null;
               }

               @Override
               public void put(LGPower power) {

               }

               @Override
               public boolean has(Class<? extends LGPower> powerClass) {
                   return false;
               }

               @Override
               public boolean remove(Class<? extends LGPower> powerClass) {
                   return false;
               }
           };
        }

        @Override
        public void changeCard(LGCard newCard) {

        }

        @Override
        public boolean willDie() {
            return false;
        }

        @Override
        public void die(LGKillCause cause) {

        }

        @Override
        public void dieLater(LGKillCause cause) {

        }

        @Override
        public void cancelFutureDeath() {

        }

        @Override
        public MetadataMap metadata() {
            return metadataMap;
        }
    }
}
