package com.github.jeuxjeux20.loupsgarous.game;

import org.bukkit.entity.Player;

public interface LGScoreboardManager {
    void updatePlayer(LGPlayer player, LGGameOrchestrator orchestrator);

    void removePlayer(Player player);

    default void removePlayer(LGPlayer player) {
        player.getMinecraftPlayer().ifPresent(this::removePlayer);
    }
}
