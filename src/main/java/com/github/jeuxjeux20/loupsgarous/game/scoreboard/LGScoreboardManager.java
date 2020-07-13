package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import org.bukkit.entity.Player;

public interface LGScoreboardManager {
    void updatePlayer(LGPlayer player, LGGameOrchestrator orchestrator);

    void removePlayer(Player player);

    default void removePlayer(LGPlayer player) {
        player.getMinecraftPlayerNoContext().ifPresent(this::removePlayer);
    }
}
