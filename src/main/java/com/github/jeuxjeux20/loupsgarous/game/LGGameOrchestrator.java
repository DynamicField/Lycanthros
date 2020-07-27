package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.actionbar.LGActionBarManager;
import com.github.jeuxjeux20.loupsgarous.bossbar.LGBossBarManager;
import com.github.jeuxjeux20.loupsgarous.cards.composition.Composition;
import com.github.jeuxjeux20.loupsgarous.chat.LGChatOrchestrator;
import com.github.jeuxjeux20.loupsgarous.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.event.LGEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGGameFinishedEvent;
import com.github.jeuxjeux20.loupsgarous.event.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.interaction.InteractableRegistry;
import com.github.jeuxjeux20.loupsgarous.kill.LGKillsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.lobby.LGGameBootstrapData;
import com.github.jeuxjeux20.loupsgarous.lobby.LGLobby;
import com.github.jeuxjeux20.loupsgarous.lobby.LobbyCreationException;
import com.github.jeuxjeux20.loupsgarous.stages.LGStagesOrchestrator;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.common.collect.ImmutableMultiset;
import me.lucko.helper.metadata.MetadataKey;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Manages a Loups-Garous game instance.
 * <p>
 * The game {@link #state()} can be changed using the appropriate methods: {@link #initialize()},
 * {@link #start()}, {@link #finish(LGEnding)} and {@link #delete()}.
 * <p>
 * This also implements {@link TerminableConsumer}, where all the bound terminables get terminated
 * as soon as the orchestrator is in the {@link LGGameState#DELETING} state.
 * <p>
 * Other aspects of the game can be used using components that break up features into multiple
 * methods:
 * <ul>
 *     <li>{@link #chat()}: Send messages using channels. ({@link LGChatOrchestrator}) </li>
 *     <li>{@link #stages()}: Manages the stages of the game. ({@link LGStagesOrchestrator})</li>
 *     <li>{@link #lobby()}: Manages the lobby and the composition of the game. ({@link LGLobby})</li>
 *     <li>{@link #kills()}: Kill people instantly, or at a later time. ({@link LGKillsOrchestrator})</li>
 * </ul>
 *
 * @author jeuxjeux20
 * @see LGChatOrchestrator
 * @see LGStagesOrchestrator
 * @see LGKillsOrchestrator
 * @see LGLobby
 * @see LGGameState
 */
public interface LGGameOrchestrator extends TerminableConsumer {
    LGGame game();

    Plugin plugin();

    default LGGameState state() {
        return game().getState();
    }

    default World world() {
        return lobby().getWorld();
    }

    default boolean isGameRunning() {
        return state() == LGGameState.STARTED;
    }


    /**
     * Initializes the game to be ready to accept new players. Usually, this method is called
     * internally. This will change the state to {@link LGGameState#WAITING_FOR_PLAYERS} or {@link
     * LGGameState#READY_TO_START}.
     *
     * @throws IllegalStateException when the game is not {@linkplain LGGameState#UNINITIALIZED
     *                               uninitialized}
     */
    void initialize();

    /**
     * Starts the game and calls the {@link LGGameStartEvent}. This will change the state to {@link
     * LGGameState#STARTED}.
     *
     * @throws IllegalStateException when the game is not {@linkplain LGGameState#READY_TO_START
     *                               ready to start}
     */
    void start();

    /**
     * Finishes the game with the given ending and calls the {@link LGGameFinishedEvent}. This will
     * change the state to {@link LGGameState#FINISHED}.
     *
     * @param ending why the game ended
     * @throws IllegalStateException when the game is {@linkplain LGGameState#UNINITIALIZED
     *                               uninitialized}, {@linkplain LGGameState#FINISHED finished},
     *                               {@linkplain LGGameState#DELETING deleting} or {@linkplain
     *                               LGGameState#DELETED deleted}
     */
    void finish(LGEnding ending);

    /**
     * Deletes the game and calls the deletion events.
     * <p>
     * This changes the state to {@link LGGameState#DELETING}, terminates every terminable bound to
     * this orchestrator, teleports all players to the spawn, and finally changes the state to
     * {@link LGGameState#DELETED}.
     *
     * @throws IllegalStateException when the game is {@linkplain LGGameState#DELETING deleting} or
     *                               {@linkplain LGGameState#DELETED deleted}
     */
    void delete();

    void nextTimeOfDay();


    <T extends OrchestratorComponent> T component(MetadataKey<T> key);

    default LGChatOrchestrator chat() {
        return component(LGComponents.CHAT);
    }

    default LGStagesOrchestrator stages() {
        return component(LGComponents.STAGES);
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

    LGLobby lobby();

    OrchestratorScope.Block scope();

    Logger logger();


    default boolean isMyEvent(LGEvent event) {
        return event.getOrchestrator() == this;
    }

    default Stream<Player> getAllMinecraftPlayers() {
        return game().getPlayers().stream().map(LGPlayer::minecraft).flatMap(OptionalUtils::stream);
    }

    default void showSubtitle(String subtitle) {
        getAllMinecraftPlayers().forEach(player -> player.sendTitle("", subtitle, -1, -1, -1));
    }

    default Composition getCurrentComposition() {
        if (state() == LGGameState.WAITING_FOR_PLAYERS || state() == LGGameState.READY_TO_START) {
            return lobby().composition().get();
        } else {
            return () -> game().getAlivePlayers().map(LGPlayer::getCard)
                    .collect(ImmutableMultiset.toImmutableMultiset());
        }
    }

    interface Factory {
        LGGameOrchestrator create(LGGameBootstrapData lobbyInfo) throws LobbyCreationException;
    }
}
