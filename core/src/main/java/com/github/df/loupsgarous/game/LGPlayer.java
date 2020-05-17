package com.github.df.loupsgarous.game;

import com.github.df.loupsgarous.UserFriendlyNamed;
import com.github.df.loupsgarous.cards.LGCard;
import com.github.df.loupsgarous.kill.causes.LGKillCause;
import com.github.df.loupsgarous.powers.PowerRegistry;
import com.github.df.loupsgarous.storage.StorageProvider;
import com.github.df.loupsgarous.tags.TagRegistry;
import com.github.df.loupsgarous.teams.TeamRegistry;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface LGPlayer extends UserFriendlyNamed, OrchestratorAware, StorageProvider {
    boolean isDead();

    default boolean isAlive() {
        return !isDead();
    }

    TeamRegistry teams();

    TagRegistry tags();

    PowerRegistry powers();

    void setCard(LGCard card);

    boolean isGoingToDie();

    default boolean isNotGoingToDie() {
        return !isGoingToDie();
    }

    void die(LGKillCause cause);

    void dieLater(LGKillCause cause);

    void cancelFutureDeath();

    UUID getPlayerUUID();

    LGCard getCard();

    default boolean isCardVisibleFor(LGPlayer viewer) {
        return getCard().isRevealed(getOrchestrator(), this, viewer);
    }

    boolean isAway();

    default boolean isPresent() {
        return !isAway();
    }

    default String getName() {
        String name = minecraftOffline().getName();
        return name == null ? "[Inconnu]" : name;
    }

    @Override
    default String getUserFriendlyName() {
        return getName();
    }

    default void sendMessage(String message) {
        minecraft(player -> player.sendMessage(message));
    }

    default void sendMessage(TextComponent message) {
        minecraft(player -> Text.sendMessage(player, message));
    }

    /**
     * Gets the minecraft player this player is linked to, if the player is away,
     * this returns {@link Optional#empty()}.
     *
     * @return the player
     */
    default Optional<Player> minecraft() {
        if (isAway()) return Optional.empty();
        return Optional.ofNullable(Bukkit.getPlayer(getPlayerUUID()));
    }

    default void minecraft(Consumer<? super Player> playerConsumer) {
        minecraft().ifPresent(playerConsumer);
    }

    /**
     * Gets the minecraft player this player is linked to, without taking account
     * of the context (e.g. the player is away).
     *
     * @return the player
     */
    default Optional<Player> minecraftNoContext() {
        return Optional.ofNullable(Bukkit.getPlayer(getPlayerUUID()));
    }

    default void minecraftNoContext(Consumer<? super Player> playerConsumer) {
        minecraftNoContext().ifPresent(playerConsumer);
    }

    /**
     * Gets the minecraft player this player is linked to, even if it is offline.
     *
     * @return the player, offline or not
     * @implSpec The default implementation uses {@link Bukkit#getOfflinePlayer(UUID)}.
     */
    default OfflinePlayer minecraftOffline() {
        return Bukkit.getOfflinePlayer(getPlayerUUID());
    }
}
