package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.kill.causes.LGKillCause;
import com.github.jeuxjeux20.loupsgarous.powers.LGPower;
import com.github.jeuxjeux20.loupsgarous.powers.PowerRegistry;
import com.github.jeuxjeux20.loupsgarous.tags.LGTag;
import com.github.jeuxjeux20.loupsgarous.tags.TagRegistry;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.TeamRegistry;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.metadata.MetadataMap;

import java.util.Optional;
import java.util.UUID;

public interface LGPlayer extends BasicLGPlayer {
    LGPlayer NULL = Null.INSTANCE;

    boolean isDead();

    default boolean isAlive() {
        return !isDead();
    }

    TeamRegistry teams();

    TagRegistry tags();

    PowerRegistry powers();

    void changeCard(LGCard card);

    boolean willDie();

    default boolean willNotDie() {
        return !willDie();
    }

    void die(LGKillCause cause);

    void dieLater(LGKillCause cause);

    void cancelFutureDeath();

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
            return LGCard.UNKNOWN;
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
        public void changeCard(LGCard card) {

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
