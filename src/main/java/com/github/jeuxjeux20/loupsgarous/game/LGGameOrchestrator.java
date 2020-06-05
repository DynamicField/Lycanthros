package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCardsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.chat.AnonymizedChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.chat.LGChatManager;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.killreasons.LGKillReason;
import com.github.jeuxjeux20.loupsgarous.game.lobby.CannotCreateLobbyException;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGGameLobby;
import com.github.jeuxjeux20.loupsgarous.game.lobby.LGGameLobbyInfo;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGStagesOrchestrator;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Manages a Loups-Garous game instance.
 * <p>
 * Orchestrators contain additional state apart from {@link #getGame()} to ensure that the game runs correctly,
 * with {@link #getState()} and {@link #stages()}.
 * <p>
 * The {@linkplain LGGameState game state} can be changed using the appropriate methods:
 * {@link #initialize()}, {@link #start()}, {@link #finish(LGEnding)} and {@link #delete()}.
 * <p>
 * While the game is running (this can be checked using {@link #isGameRunning()}),
 * people can be killed using methods such as {@link #getPendingKills()}, {@link #revealAllPendingKills()}
 * and {@link #killInstantly(LGKill)}.
 * <p>
 * This also implements {@link TerminableConsumer}, where all the bound terminables get terminated
 * as soon as the orchestrator is in the {@link LGGameState#DELETING} state.
 * <p>
 * Other aspects of the game can be used using components that break up features into multiple objects:
 * <ul>
 *     <li>{@link #chat()}: Send messages using channels. ({@link LGChatManager}) </li>
 *     <li>{@link #stages()}: Manages the stages of the game. ({@link LGStagesOrchestrator})</li>
 *     <li>{@link #cards()}: Manages cards and their teams. ({@link LGCardsOrchestrator})</li>
 *     <li>{@link #lobby()}: Manages the lobby and the composition of the game. ({@link LGGameLobby})</li>
 * </ul>
 *
 * @see LGChatManager
 * @see LGStagesOrchestrator
 * @see LGCardsOrchestrator
 * @see LGGameLobby
 * @see LGGameState
 * @author jeuxjeux20
 */
public interface LGGameOrchestrator extends TerminableConsumer {
    Plugin getPlugin();

    World getWorld();

    LGGame getGame();

    String getId();

    LGGameState getState();

    Optional<LGEnding> getEnding();

    HashMap<AnonymizedChatChannel, List<String>> getAnonymizedNames();

    default boolean isGameRunning() {
        return getState() == LGGameState.STARTED;
    }

    default LGGameTurn getTurn() {
        return getGame().getTurn();
    }


    void initialize();

    void start();

    void finish(LGEnding ending);

    void delete();

    void nextTimeOfDay();


    Set<LGKill> getPendingKills();

    void revealAllPendingKills();

    void killInstantly(LGKill lgKill);

    default void killInstantly(LGPlayer player, LGKillReason reason) {
        killInstantly(LGKill.of(player, reason));
    }

    default void killInstantly(LGPlayer player, Supplier<LGKillReason> reasonSupplier) {
        killInstantly(LGKill.of(player, reasonSupplier));
    }

    void callEvent(LGEvent event);

    default Stream<@NotNull Player> getAllMinecraftPlayers() {
        return getGame().getPlayers().stream().map(LGPlayer::getMinecraftPlayer).flatMap(OptionalUtils::stream);
    }

    default void sendToEveryone(String message) {
        getAllMinecraftPlayers().forEach(player -> player.sendMessage(message));
    }

    default void showSubtitle(String subtitle) {
        getAllMinecraftPlayers().forEach(player -> player.sendTitle("", subtitle, -1, -1, -1));
    }

    default Stream<LGCard> getCurrentComposition() {
        if (getState() == LGGameState.WAITING_FOR_PLAYERS || getState() == LGGameState.READY_TO_START) {
            return lobby().getComposition().getCards().stream();
        } else {
            return getGame().getAlivePlayers().map(LGPlayer::getCard);
        }
    }

    LGChatManager chat();

    LGStagesOrchestrator stages();

    LGCardsOrchestrator cards();

    LGGameLobby lobby();

    interface Factory {
        LGGameOrchestrator create(LGGameLobbyInfo lobbyInfo) throws CannotCreateLobbyException;
    }
}
