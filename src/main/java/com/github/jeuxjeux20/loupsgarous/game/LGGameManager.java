package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LGGameManager {
    String WORLD_PREFIX = "lg_game_";

    SafeResult<LGGameOrchestrator> startGame(Set<Player> players, Composition composition);

    ImmutableList<LGGameOrchestrator> getOngoingGames();

    Optional<LGPlayerAndGame> getPlayerInGame(UUID playerUUID);

    default Optional<LGPlayerAndGame> getPlayerInGame(Player player) {
        return getPlayerInGame(player.getUniqueId());
    }

    Optional<LGGameOrchestrator> getGameById(String id);
}
