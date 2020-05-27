package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.cards.LGCardOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.chat.AnonymizedChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.killreasons.LGKillReason;
import com.github.jeuxjeux20.loupsgarous.game.stages.AsyncLGGameStage;
import com.github.jeuxjeux20.loupsgarous.game.stages.LGGameStage;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import me.lucko.helper.terminable.TerminableConsumer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Manages a Loups-Garous game instance.
 * <p>
 * Orchestrators manage the game state, with methods such as {@link #killInstantly(LGKill)}, {@link #nextTimeOfDay()}.
 */
public interface LGGameOrchestrator extends TerminableConsumer {
    Plugin getPlugin();

    MultiverseWorld getWorld();

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

    /**
     * Adds a stage to the current game.
     * If the game has started, the stage added will be executed as soon as possible,
     * else, it will be added at the end.
     *
     * @param stage the stage to add
     */
    void addStage(AsyncLGGameStage.Factory<?> stage);

    @NotNull LGGameStage getCurrentStage();


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

    default Stream<LGCard> getComposition() {
        if (getState() == LGGameState.WAITING_FOR_PLAYERS || getState() == LGGameState.READY_TO_START) {
            return lobby().getComposition().getCards().stream();
        } else {
            return getGame().getAlivePlayers().map(LGPlayer::getCard);
        }
    }

    LGCardOrchestrator cards();

    LGGameLobby lobby();

    interface Factory {
        LGGameOrchestrator create(LGGameLobbyInfo worldInfo);
    }
}
