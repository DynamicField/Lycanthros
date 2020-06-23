package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.lobby.PlayerJoinException;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface LGGameManager {
    LGGameOrchestrator start(Player owner, Composition composition, @Nullable String id) throws GameCreationException;

    default LGGameOrchestrator start(Player owner, Composition composition) throws GameCreationException {
        return start(owner, composition, null);
    }

    default void joinOrStart(Player player, Composition composition, String id)
            throws GameCreationException, PlayerJoinException {
        Optional<LGGameOrchestrator> existingGame = get(id);

        if (existingGame.isPresent()) {
            existingGame.get().lobby().addPlayer(player);
        } else {
            start(player, composition, id);
        }
    }

    Optional<LGGameOrchestrator> get(String id);

    ImmutableList<LGGameOrchestrator> getAll();


    Optional<LGPlayerAndGame> getPlayerInGame(UUID playerUUID);

    default Optional<LGPlayerAndGame> getPlayerInGame(Player player) {
        return getPlayerInGame(player.getUniqueId());
    }


}
