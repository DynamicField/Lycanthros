package com.github.jeuxjeux20.loupsgarous.game.scoreboard;

import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.OrchestratorComponent;
import org.bukkit.entity.Player;

public interface LGScoreboardManager extends OrchestratorComponent {
    void updatePlayer(LGPlayer player);

    void removePlayer(Player player);

    default void removePlayer(LGPlayer player) {
        player.getMinecraftPlayerNoContext().ifPresent(this::removePlayer);
    }
}
