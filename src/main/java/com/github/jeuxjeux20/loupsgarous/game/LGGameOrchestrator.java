package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.LoupsGarous;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCardsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatManager;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.kill.LGKillsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.lobby.CannotCreateLobbyException;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGGameLobby;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGGameLobbyInfo;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStagesOrchestrator;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.READY_TO_START;
import static com.github.jeuxjeux20.loupsgarous.game.LGGameState.STARTED;

/**
 * Manages a Loups-Garous game instance.
 * <p>
 * Orchestrators contain additional state apart from the {@link #game()} to ensure that the game runs correctly,
 * with the {@link #state()} and {@link #stages()}.
 * <p>
 * The {@linkplain LGGameState game state} can be changed using the appropriate methods:
 * {@link #initialize()}, {@link #start()}, {@link #finish(LGEnding)} and {@link #delete()}.
 * <p>
 * This also implements {@link TerminableConsumer}, where all the bound terminables get terminated
 * as soon as the orchestrator is in the {@link LGGameState#DELETING} state.
 * <p>
 * Other aspects of the game can be used using components that break up features into multiple methods:
 * <ul>
 *     <li>{@link #chat()}: Send messages using channels. ({@link LGChatManager}) </li>
 *     <li>{@link #stages()}: Manages the stages of the game. ({@link LGStagesOrchestrator})</li>
 *     <li>{@link #cards()}: Manages cards and their teams. ({@link LGCardsOrchestrator})</li>
 *     <li>{@link #lobby()}: Manages the lobby and the composition of the game. ({@link LGGameLobby})</li>
 *     <li>{@link #kills()}: Kill people instantly, or at a later time. ({@link LGKillsOrchestrator})</li>
 * </ul>
 *
 * @author jeuxjeux20
 * @see LGChatManager
 * @see LGStagesOrchestrator
 * @see LGCardsOrchestrator
 * @see LGKillsOrchestrator
 * @see LGGameLobby
 * @see LGGameState
 */
public interface LGGameOrchestrator extends TerminableConsumer {
    LGGame game();

    LGGameState state();

    LoupsGarous plugin();

    default LGGameTurn turn() {
        return game().getTurn();
    }

    default World world() {
        return lobby().getWorld();
    }

    default boolean isGameRunning() {
        return state() == STARTED;
    }


    void initialize();

    void start();

    void finish(LGEnding ending);

    void delete();

    void nextTimeOfDay();


    void callEvent(LGEvent event);


    default Stream<@NotNull Player> getAllMinecraftPlayers() {
        return game().getPlayers().stream().map(LGPlayer::getMinecraftPlayer).flatMap(OptionalUtils::stream);
    }

    default void showSubtitle(String subtitle) {
        getAllMinecraftPlayers().forEach(player -> player.sendTitle("", subtitle, -1, -1, -1));
    }

    default Stream<LGCard> getCurrentComposition() {
        if (state() == LGGameState.WAITING_FOR_PLAYERS || state() == READY_TO_START) {
            return lobby().getComposition().getCards().stream();
        } else {
            return game().getAlivePlayers().map(LGPlayer::getCard);
        }
    }

    LGChatManager chat();

    LGStagesOrchestrator stages();

    LGCardsOrchestrator cards();

    LGKillsOrchestrator kills();

    LGGameLobby lobby();

    interface Factory {
        LGGameOrchestrator create(LGGameLobbyInfo lobbyInfo) throws CannotCreateLobbyException;
    }
}
