package com.github.jeuxjeux20.loupsgarous.game;

import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public interface LGGameManager {
    String WORLD_PREFIX = "lg_game_";
    int SHORT_ID_LENGTH = 8;

    SafeResult<LGGameOrchestrator> startGame(String worldToClone, List<Player> players, CommandSender teleporter);

    List<LGGameOrchestrator> getOngoingGames();

    default Optional<LGPlayerAndGame> getPlayerInGame(Player player) {
        Optional<Player> playerOptional = Optional.of(player);

        for (LGGameOrchestrator game : getOngoingGames()) {
            Optional<LGPlayer> lgPlayer = game.getGame().getPlayers().stream()
                    .filter(x -> x.getMinecraftPlayer().equals(playerOptional))
                    .findFirst();
            return lgPlayer.map(p -> new LGPlayerAndGame(p, game));
        }
        return Optional.empty();
    }
}
