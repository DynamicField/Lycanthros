package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface LGGameManager {
    String WORLD_PREFIX = "lg_game_";
    int SHORT_ID_LENGTH = 8;

    SafeResult<LGGameOrchestrator> startGame(String worldToClone, Set<Player> players,
                                             Composition composition, CommandSender owner);

    ImmutableList<LGGameOrchestrator> getOngoingGames();

    Optional<LGPlayerAndGame> getPlayerInGame(UUID playerUUID);

    default Optional<LGPlayerAndGame> getPlayerInGame(Player player) {
        return getPlayerInGame(player.getUniqueId());
    }

    Optional<LGGameOrchestrator> getGameById(String id);
}
