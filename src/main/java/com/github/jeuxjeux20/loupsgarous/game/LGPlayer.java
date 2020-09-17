package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.MetadataContainer;
import com.github.jeuxjeux20.loupsgarous.UserFriendlyNamed;
import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.kill.causes.LGKillCause;
import com.github.jeuxjeux20.loupsgarous.powers.PowerRegistry;
import com.github.jeuxjeux20.loupsgarous.tags.TagRegistry;
import com.github.jeuxjeux20.loupsgarous.teams.TeamRegistry;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface LGPlayer extends UserFriendlyNamed, MetadataContainer, OrchestratorDependent {
    boolean isDead();

    default boolean isAlive() {
        return !isDead();
    }

    TeamRegistry teams();

    TagRegistry tags();

    PowerRegistry powers();

    void setCard(LGCard card);

    boolean willDie();

    default boolean willNotDie() {
        return !willDie();
    }

    void die(LGKillCause cause);

    void dieLater(LGKillCause cause);

    void cancelFutureDeath();

    UUID getPlayerUUID();

    LGCard getCard();

    default boolean isCardVisibleFor(LGPlayer viewer) {
        return getCard().isRevealed(getOrchestrator(), this, viewer);
    }

    MetadataMap metadata();

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
