package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.google.common.collect.ImmutableSet;

import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class UndeterminedLGGame<T> implements LGGame {
    private static final UndeterminedLGGameTurn UNDETERMINED_TURN = new UndeterminedLGGameTurn();

    public static <T> UndeterminedLGGame<T> of(Supplier<T> valueSupplier, Supplier<Stream<UUID>> playerUUIDsSupplier) {
        return new UndeterminedLGGame<T>() {
            @Override
            public T value() {
                return valueSupplier.get();
            }

            @Override
            public Stream<UUID> getPlayerUUIDs() {
                return playerUUIDsSupplier.get();
            }
        };
    }

    public abstract T value();

    public abstract Stream<UUID> getPlayerUUIDs();

    @Override
    public ImmutableSet<LGPlayer> getPlayers() {
        return getPlayerUUIDs().map(UndeterminedLGPlayer::new).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public LGGameTurn getTurn() {
        return UNDETERMINED_TURN;
    }

    private static final class UndeterminedLGPlayer implements LGPlayer {
        private final UUID playerUUID;

        public UndeterminedLGPlayer(UUID playerUUID) {
            this.playerUUID = playerUUID;
        }

        @Override
        public UUID getPlayerUUID() {
            return playerUUID;
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
            return false;
        }
    }

    private static final class UndeterminedLGGameTurn implements LGGameTurn {
        @Override
        public LGGameTurnTime getTime() {
            return LGGameTurnTime.DAY;
        }

        @Override
        public int getTurnNumber() {
            return 0;
        }

    }

}
