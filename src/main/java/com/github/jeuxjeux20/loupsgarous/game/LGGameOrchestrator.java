package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCardsOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.chat.AnonymizedChatChannel;
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
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Manages a Loups-Garous game instance.
 * <p>
 * Orchestrators manage the game state, with methods such as {@link #killInstantly(LGKill)}, {@link #nextTimeOfDay()}.
 */
public interface LGGameOrchestrator extends TerminableConsumer {
    Plugin getPlugin();

    World getWorld();

    LGGame getGame();

    String getId();

    LGGameState getState();

    default boolean isGameRunning() {
        return getState() == LGGameState.STARTED;
    }


    void initialize();

    void start();

    void finish(LGEnding ending);

    void delete();

    void nextTimeOfDay();


    List<LGKill> getPendingKills();

    void revealAllPendingKills();

    void killInstantly(LGKill lgKill);

    default void killInstantly(LGPlayer player, LGKillReason reason) {
        killInstantly(LGKill.of(player, reason));
    }

    default void killInstantly(LGPlayer player, Supplier<LGKillReason> reasonSupplier) {
        killInstantly(LGKill.of(player, reasonSupplier));
    }

    Optional<LGEnding> getEnding();


    void callEvent(LGEvent event);

    HashMap<AnonymizedChatChannel, List<String>> getAnonymizedNames();

    default Stream<@NotNull Player> getAllMinecraftPlayers() {
        return getGame().getPlayers().stream().map(LGPlayer::getMinecraftPlayer).flatMap(OptionalUtils::stream);
    }

    default LGGameTurn getTurn() {
        return getGame().getTurn();
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

    LGStagesOrchestrator stages();

    LGCardsOrchestrator cards();

    LGGameLobby lobby();

    interface Factory {
        LGGameOrchestrator create(LGGameLobbyInfo lobbyInfo) throws CannotCreateLobbyException;
    }
}
