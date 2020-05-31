package com.github.jeuxjeux20.loupsgarous.game.lobby;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.MutableLGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.validation.CompositionValidator;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface LGGameLobby {
    World getWorld();

    boolean addPlayer(Player player);

    boolean removePlayer(UUID playerUUID);

    default boolean removePlayer(OfflinePlayer player) {
        return removePlayer(player.getUniqueId());
    }

    default boolean removePlayer(LGPlayer player) {
        return removePlayer(player.getPlayerUUID());
    }

    boolean isLocked();

    Optional<MutableComposition> getMutableComposition();

    Composition getComposition();

    @Nullable CompositionValidator.Problem.Type getWorseCompositionProblemType();

    default boolean isCompositionValid() {
        return getWorseCompositionProblemType() != CompositionValidator.Problem.Type.IMPOSSIBLE;
    }

    LGGameOrchestrator gameOrchestrator();

    default int getSlotsTaken() {
        return (int) gameOrchestrator().getGame().getPresentPlayers().count();
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

    Player getOwner();

    void setOwner(Player owner);

    interface Factory {
        LGGameLobby create(LGGameLobbyInfo lobbyInfo, MutableLGGameOrchestrator orchestrator)
                throws CannotCreateLobbyException;
    }
}
