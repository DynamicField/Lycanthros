package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.endings.FinishCommandEnding;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Collectors;

@CommandName("lgfinish")
public class LGFinishCommand implements AnnotatedCommandConfigurator {
    private final LGGameManager gameManager;

    @Inject
    LGFinishCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        Commands.create()
                .assertPermission("loupsgarous.game.finish",
                        ChatColor.RED + "Vous n'avez pas la permission de terminer les parties :(")
                .assertUsage("[game] [reason]", "{usage}")
                .handler(c -> {
                    Optional<LGGameOrchestrator> game = Optional.empty();
                    String gameId = c.arg(0).value().orElse(null);
                    if (gameId != null) game = gameManager.getGameById(gameId);

                    boolean hasTried = gameId != null;
                    boolean hasProvidedGame = game.isPresent();

                    if (!hasProvidedGame) {
                        if (c.sender() instanceof Player) {
                            Player sender = (Player) c.sender();
                            game = gameManager.getPlayerInGame(sender).map(LGPlayerAndGame::getOrchestrator);
                        }
                        if (!game.isPresent()) {
                            if (hasTried) {
                                c.reply(ChatColor.RED + "Impossible de trouver la partie " + gameId + ".");
                            } else {
                                c.reply(ChatColor.RED + "Vous n'êtes pas en partie. Pour terminer une partie," +
                                        " utilisez /lglist pour avoir les parties et /lgfinish <partie> pour la terminer.");
                            }

                            return;
                        }
                    }

                    if (game.get().getState() == LGGameState.FINISHED) {
                        c.sender().sendMessage(ChatColor.RED + "La partie est déjà terminée.");
                        return;
                    }

                    String reason = hasProvidedGame && c.args().size() == 1 || c.args().size() == 0 ? null :
                            c.args().stream().skip(hasProvidedGame ? 1 : 0).collect(Collectors.joining(" "));

                    game.get().finish(new FinishCommandEnding(reason));
                })
                .register(getCommandName());
    }
}
