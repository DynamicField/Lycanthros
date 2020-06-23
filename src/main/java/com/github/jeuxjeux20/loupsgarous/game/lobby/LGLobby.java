package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestratorComponent;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Manages players entering in and out of a game, as well as the {@link Composition} of the game.
 */
public interface LGLobby extends LGGameOrchestratorComponent {
    World getWorld();

    LGPlayer addPlayer(Player player) throws PlayerJoinException;

    boolean removePlayer(UUID playerUUID);

    default boolean removePlayer(OfflinePlayer player) {
        return removePlayer(player.getUniqueId());
    }

    default boolean removePlayer(LGPlayer player) {
        return removePlayer(player.getPlayerUUID());
    }

    boolean isLocked();

    void openOwnerGui();

    Composition getComposition();

    @Nullable CompositionValidator.Problem.Type getWorstCompositionProblemType();

    default boolean isCompositionValid() {
        return getWorstCompositionProblemType() != CompositionValidator.Problem.Type.IMPOSSIBLE;
    }

    default int getSlotsTaken() {
        return (int) gameOrchestrator().game().getPresentPlayers().count();
    }

    default int getTotalSlotCount() {
        return getComposition().getPlayerCount();
    }

    default boolean isFull() {
        return getSlotsTaken() == getTotalSlotCount();
    }

    default String getSlotsDisplay() {
        return "(" + getSlotsTaken() + "/" + getTotalSlotCount() + ")";
    }

    LGPlayer getOwner();

    void setOwner(LGPlayer owner);

    interface Factory {
        LGLobby create(LGGameBootstrapData lobbyInfo, MutableLGGameOrchestrator orchestrator)
                throws LobbyCreationException;
    }
}
