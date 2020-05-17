package com.github.jeuxjeux20.loupsgarous.game;

import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class UndeterminedLGGame implements LGGame {
    private final ImmutableSet<LGPlayer> players;

    public UndeterminedLGGame(Collection<UUID> playerUUIDs) {
        this.players = createPlayerSet(playerUUIDs);
    }

    private ImmutableSet<LGPlayer> createPlayerSet(Collection<UUID> playerUUIDs) {
        return playerUUIDs.stream()
                .map(UndeterminedLGPlayer::new)
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public ImmutableSet<LGPlayer> getPlayers() {
        return players;
    }

    @Override
    public LGGameTurn getTurn() {
        return new LGGameTurn() {
            @Override
            public LGGameTurnTime getTime() {
                return LGGameTurnTime.DAY;
            }

            @Override
            public int getTurnNumber() {
                return 0;
            }
        };
    }
}
