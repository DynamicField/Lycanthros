package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.MetadataContainer;
import com.github.jeuxjeux20.loupsgarous.UserFriendlyNamed;
import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import me.lucko.helper.metadata.MetadataMap;
import me.lucko.helper.text.Text;
import me.lucko.helper.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface BasicLGPlayer extends UserFriendlyNamed, MetadataContainer {
    UUID getPlayerUUID();

    LGCard getCard();

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
