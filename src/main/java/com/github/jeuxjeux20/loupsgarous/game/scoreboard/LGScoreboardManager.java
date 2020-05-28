package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface LGScoreboardManager {
    void registerEvents();

    void updatePlayer(LGPlayer player, LGGameOrchestrator orchestrator);

    void removePlayer(Player player);

    default void removePlayer(LGPlayer player) {
        OfflinePlayer offlinePlayer = player.getOfflineMinecraftPlayer();
        Player onlinePlayer = offlinePlayer.getPlayer();
        if (onlinePlayer == null) return;

        removePlayer(onlinePlayer);
    }
}
