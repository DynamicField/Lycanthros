package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.MutableComposition;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface LGGameLobby {
    boolean canAddPlayer();

    boolean canRemovePlayer();

    boolean addPlayer(Player player);

    boolean removePlayer(Player player);

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
