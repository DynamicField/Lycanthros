package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarousRoot;
import com.github.jeuxjeux20.loupsgarous.actionbar.LGActionBarManager;
import com.github.jeuxjeux20.loupsgarous.bossbar.LGBossBarManager;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.cards.composition.ImmutableComposition;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatOrchestrator;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGGameFinishedEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.extensibility.GameBox;
import com.github.jeuxjeux20.loupsgarous.interaction.InteractableRegistry;
import com.github.jeuxjeux20.loupsgarous.kill.LGKillsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.lobby.LGGameBootstrapData;
import com.github.jeuxjeux20.loupsgarous.lobby.PlayerJoinException;
import com.github.jeuxjeux20.loupsgarous.phases.LGPhasesOrchestrator;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Provider;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.metadata.MetadataMap;
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
 * <p>
 * The game state can be changed using the appropriate methods: {@link #initialize(LGGameBootstrapData)}, {@link
 * #start()}, {@link #finish(LGEnding)} and {@link #delete()}.
 * <p>
 * This also implements {@link TerminableConsumer}, where all the bound terminables get terminated
 * as soon as the orchestrator is in the {@link LGGameState#DELETING} state.
 * <p>
 * Other aspects of the game can be used using components that break up features into multiple
 * methods:
 * <ul>
 *     <li>{@link #chat()}: Send messages using channels. ({@link LGChatOrchestrator}) </li>
 *     <li>{@link #phases()}: Manages the phases of the game. ({@link LGPhasesOrchestrator})</li>
 *     <li>{@link #kills()}: Kill people instantly, or at a later time. ({@link LGKillsOrchestrator})</li>
 * </ul>
 *
 * @author jeuxjeux20
 * @see LGChatOrchestrator
 * @see LGPhasesOrchestrator
 * @see LGKillsOrchestrator
 * @see LGGameState
 */
public interface LGGameOrchestrator extends TerminableConsumer {
    LoupsGarousRoot getLoupsGarous();

    World getWorld();

    default boolean isGameRunning() {
        return getState() == LGGameState.STARTED;
    }


    /**
     * Initializes the game to be ready to accept new players. Usually, this method is called
     * internally. This will change state to {@link LGGameState#LOBBY}.
     *
     * @param bootstrapData the data used to initialize the game
     */
    void initialize(LGGameBootstrapData bootstrapData) throws GameCreationException;

    /**
     * Starts the game and calls the {@link LGGameStartEvent}. This will change state to {@link
     * LGGameState#STARTED}.
     *
     * @throws IllegalStateException when the game state is not in the {@linkplain
     *                               LGGameState#LOBBY} state.
     */
    void start();

    /**
     * Finishes the game with the given ending and calls the {@link LGGameFinishedEvent}. This will
     * change state to {@link LGGameState#FINISHED}.
     *
     * @param ending why the game ended
     * @throws IllegalStateException when the game is {@linkplain LGGameState#FINISHED finished},
     *                               {@linkplain LGGameState#DELETING deleting} or {@linkplain
     *                               LGGameState#DELETED deleted}
     */
    void finish(LGEnding ending);

    /**
     * Deletes the game and calls the deletion events.
     * <p>
     * This changes state to {@link LGGameState#DELETING}, terminates every terminable bound to this
     * orchestrator, teleports all players to the spawn, and finally changes state to {@link
     * LGGameState#DELETED}.
     *
     * @throws IllegalStateException when the game is {@linkplain LGGameState#DELETING deleting} or
     *                               {@linkplain LGGameState#DELETED deleted}
     */
    void delete();

    void nextTimeOfDay();

    <T> T resolve(Class<T> clazz);

    <T> T resolve(Provider<T> provider);


    String getId();

    LGGameState getState();

    ImmutableSet<LGPlayer> getPlayers();

    LGGameTurn getTurn();

    @Nullable LGEnding getEnding();

    @Nullable LGPlayer getOwner();

    void setOwner(LGPlayer owner);

    GameBox getGameBox();

    MetadataMap getMetadata();

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

    <T extends OrchestratorComponent> T component(MetadataKey<T> key);

    default LGChatOrchestrator chat() {
        return component(LGComponents.CHAT);
    }

    default LGPhasesOrchestrator phases() {
        return component(LGComponents.PHASES);
    }

    default LGKillsOrchestrator kills() {
        return component(LGComponents.KILLS);
    }

    default LGActionBarManager actionBar() {
        return component(LGComponents.ACTION_BAR);
    }

    default LGBossBarManager bossBar() {
        return component(LGComponents.BOSS_BAR);
    }

    default InteractableRegistry interactables() {
        return component(LGComponents.INTERACTABLES);
    }

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
