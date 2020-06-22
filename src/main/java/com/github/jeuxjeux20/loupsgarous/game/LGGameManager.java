package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface LGGameManager {
    SafeResult<LGGameOrchestrator> startGame(Composition composition, @Nullable String id);

    default SafeResult<LGGameOrchestrator> startGame(Composition composition) {
        return startGame(composition, null);
    }

    ImmutableList<LGGameOrchestrator> getOngoingGames();

    Optional<LGPlayerAndGame> getPlayerInGame(UUID playerUUID);

    default Optional<LGPlayerAndGame> getPlayerInGame(Player player) {
        return getPlayerInGame(player.getUniqueId());
    }

    Optional<LGGameOrchestrator> getGameById(String id);

    default Optional<LGGameOrchestrator> getOrStart(Composition composition, String id) {
        return OptionalUtils.or(
                () -> getGameById(id),
                () -> startGame(composition, id).getValueOptional()
        );
    }
}
