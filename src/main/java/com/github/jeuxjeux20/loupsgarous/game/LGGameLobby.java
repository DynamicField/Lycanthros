package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public interface LGGameLobby {
    boolean addPlayer(Player player);

    boolean removePlayer(UUID playerUUID);

    default boolean removePlayer(OfflinePlayer player) {
        return removePlayer(player.getUniqueId());
    }

    boolean isLocked();

    Optional<MutableComposition> getMutableComposition();

    Composition getComposition();

    LGGameOrchestrator getOrchestrator();

    default int getSlotsTaken() {
        return getOrchestrator().getGame().getPlayers().size();
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
}
