package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.lobby.PlayerJoinException;
import com.github.jeuxjeux20.loupsgarous.util.OptionalUtils;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LGJoinCommand implements HelperCommandRegisterer {
    private final LGGameManager gameManager;

    @Inject
    LGJoinCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPermission("loupsgarous.game.join",
                        ChatColor.RED + "Vous n'avez pas la permission de rejoindre des parties :(")
                .assertUsage("<game>", "{usage}")
                .assertPlayer()
                .handler(c -> {
                    String name = c.arg(0).value().orElseThrow(AssertionError::new);

                    Optional<LGGameOrchestrator> maybeGame = OptionalUtils.or(
                            () -> gameManager.get(name),
                            () -> {
                                Player player = Bukkit.getPlayer(name);
                                if (player == null) return Optional.empty();
                                return gameManager.getPlayerInGame(player).map(LGPlayerAndGame::getOrchestrator);
                            }
                    );

                    if (!maybeGame.isPresent()) {
                        c.reply("&cImpossible de trouver la partie.");
                        return;
                    }

                    try {
                        maybeGame.get().lobby().addPlayer(c.sender());
                    } catch (PlayerJoinException e) {
                        c.reply("&cImpossible de rejoindre la partie: " + e.getLocalizedMessage());
                    }
                })
                .register("lgjoin", "lg join");
    }
}
