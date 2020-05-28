package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@CommandName("lgjoin")
public class LGJoinCommand implements AnnotatedCommandConfigurator {
    private final LGGameManager gameManager;

    @Inject
    LGJoinCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        Commands.create()
                .assertPermission("loupsgarous.game.finish",
                        ChatColor.RED + "Vous n'avez pas la permission de terminer les parties :(")
                .assertUsage("<game>", "{usage}")
                .assertPlayer()
                .handler(c -> {
                    Optional<LGGameOrchestrator> maybeGame = c.arg(0).value().flatMap(gameManager::getGameById);

                    if (!maybeGame.isPresent()) {
                        c.reply("&cImpossible de trouver la partie.");
                        return;
                    }

                    LGGameOrchestrator game = maybeGame.get();
                    if (!game.lobby().addPlayer(c.sender())) {
                        c.reply("&cImpossible de rejoindre la partie.");
                    }
                })
                .register(getCommandName());
    }
}
