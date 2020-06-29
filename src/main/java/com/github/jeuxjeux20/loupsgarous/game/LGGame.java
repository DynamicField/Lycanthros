package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.game.chat.AnonymizedChatChannel;
import com.github.jeuxjeux20.loupsgarous.game.endings.LGEnding;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface LGGame {
    String getId();

    ImmutableSet<LGPlayer> getPlayers();

    LGGameTurn getTurn();

    Map<AnonymizedChatChannel, List<String>> getAnonymizedNames();

    Optional<LGEnding> getEnding();


    Optional<? extends LGPlayer> getPlayer(UUID playerUUID);

    default Optional<? extends LGPlayer> getPlayer(LGPlayer player) {
        return getPlayer(player.getPlayerUUID());
    }

    default Optional<? extends LGPlayer> getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }


    default Stream<LGPlayer> getAlivePlayers() {
        return getPlayers().stream().filter(LGPlayer::isAlive);
    }

    default boolean isEmpty() {
        return getPlayers().size() == 0;
    }

    default Optional<LGPlayer> findByName(String name) {
        return getPlayers().stream().filter(x -> x.getName().equals(name)).findAny();
    }
}
