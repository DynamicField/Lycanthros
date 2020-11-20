package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarousRoot;
import com.github.jeuxjeux20.loupsgarous.actionbar.LGActionBarManager;
import com.github.jeuxjeux20.loupsgarous.bossbar.LGBossBarManager;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.cards.composition.ImmutableComposition;
import com.github.jeuxjeux20.loupsgarous.chat.ChatOrchestrator;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.GameBox;
import com.github.jeuxjeux20.loupsgarous.interaction.InteractableRegistry;
import com.github.jeuxjeux20.loupsgarous.inventory.LGInventoryManager;
import com.github.jeuxjeux20.loupsgarous.kill.KillsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.lobby.PlayerJoinException;
import com.github.jeuxjeux20.loupsgarous.phases.PhasesOrchestrator;
import com.github.jeuxjeux20.loupsgarous.scoreboard.LGScoreboardManager;
import com.github.jeuxjeux20.loupsgarous.storage.StorageProvider;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Manages a Loups-Garous game instance.
 *
 * @author jeuxjeux20
 */
public interface LGGameOrchestrator extends TerminableConsumer, StorageProvider {
    LoupsGarousRoot getLoupsGarous();

    World getWorld();

    default boolean isGameRunning() {
        return getState() == LGGameState.STARTED;
    }

    void nextTimeOfDay();

    String getId();

    LGGameState getState();

    ImmutableSet<LGPlayer> getPlayers();

    LGGameTurn getTurn();

    @Nullable LGEnding getEnding();

    @Nullable LGPlayer getOwner();

    void setOwner(LGPlayer owner);

    GameBox getGameBox();

    default Stream<LGPlayer> getAlivePlayers() {
        return getPlayers().stream().filter(LGPlayer::isAlive);
    }

    default boolean isEmpty() {
        for (LGPlayer player : getPlayers()) {
            if (player.isPresent()) {
                return false;
            }
        }
        return true;
    }

    Optional<? extends LGPlayer> getPlayer(UUID playerUUID);

    /**
     * Gets a player by the given UUID or throw a {@link PlayerAbsentException}.
     *
     * @param playerUUID the player's UUID
     * @return the found player
     * @throws PlayerAbsentException when the player has not been found
     */
    LGPlayer getPlayerOrThrow(UUID playerUUID);

    /**
     * Throws a {@link PlayerAbsentException} when the given player isn't present in the game.
     *
     * @param player the player
     * @return the same player
     * @throws PlayerAbsentException when the player has not been found
     */
    LGPlayer ensurePresent(LGPlayer player);

    boolean isEndingWhenEmpty();

    void setEndingWhenEmpty(boolean endingWhenEmpty);

    LGPlayer join(Player player) throws PlayerJoinException;

    boolean leave(UUID playerUUID);

    default boolean leave(OfflinePlayer player) {
        return leave(player.getUniqueId());
    }

    default boolean leave(LGPlayer player) {
        return leave(player.getPlayerUUID());
    }

    boolean allowsJoin();

    ImmutableComposition getComposition();

    void setComposition(Composition composition);

    StateTransitionHandler stateTransitions();

    PhasesOrchestrator phases();

    ChatOrchestrator chat();

    KillsOrchestrator kills();

    InteractableRegistry interactables();

    LGActionBarManager actionBar();

    LGScoreboardManager scoreboard();

    LGInventoryManager inventory();

    LGBossBarManager bossBar();

    OrchestratorComponentManager components();

    Logger logger();

    default boolean isMyEvent(LGEvent event) {
        return event.getOrchestrator() == this;
    }

    default int getPlayersCount() {
        return getPlayers().size();
    }

    default int getMaxPlayers() {
        return getComposition().getPlayerCount();
    }

    default boolean isFull() {
        return getPlayersCount() == getMaxPlayers();
    }

    default String getSlotsDisplay() {
        return "(" + getPlayersCount() + "/" + getMaxPlayers() + ")";
    }

    default Stream<Player> getAllMinecraftPlayers() {
        return getPlayers().stream().map(LGPlayer::minecraft).flatMap(OptionalUtils::stream);
    }

    default Composition getCurrentComposition() {
        if (getState() == LGGameState.LOBBY) {
            return getComposition();
        } else {
            return () -> getAlivePlayers().map(LGPlayer::getCard)
                    .collect(ImmutableMultiset.toImmutableMultiset());
        }
    }

    default Optional<LGPlayer> findByName(String name) {
        return getPlayers().stream().filter(x -> x.getName().equals(name)).findAny();
    }
}
