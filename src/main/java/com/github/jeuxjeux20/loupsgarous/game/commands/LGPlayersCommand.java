package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.LGMessages;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameState;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.banner;
import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.role;

@CommandName("lgplayers")
public class LGPlayersCommand implements AnnotatedCommandConfigurator {
    private final LGGameManager gameManager;

    @Inject
    LGPlayersCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        Commands.create()
                .assertPlayer()
                .handler(c -> {
                    Optional<LGPlayerAndGame> game = gameManager.getPlayerInGame(c.sender());
                    if (!game.isPresent()) {
                        c.reply(LGMessages.NOT_IN_GAME);
                        return;
                    }
                    Set<LGPlayer> players = game.get().getOrchestrator().getGame().getPlayers();

                    StringBuilder messageBuilder = new StringBuilder()
                            .append(banner("Liste des joueurs"))
                            .append('\n');

                    for (LGPlayer player : players) {
                        messageBuilder.append(ChatColor.RESET);

                        messageBuilder.append(ChatColor.DARK_AQUA);

                        if (player.isDead())
                            messageBuilder.append(ChatColor.STRIKETHROUGH);

                        messageBuilder.append(player.getName());

                        messageBuilder.append(ChatColor.DARK_AQUA);

                        if (player.isDead() || game.get().getOrchestrator().getState() == LGGameState.FINISHED)
                            messageBuilder.append(" (")
                                    .append(role(player.getCard().getName()))
                                    .append(ChatColor.DARK_AQUA)
                                    .append(")");

                        messageBuilder.append('\n');
                    }

                    messageBuilder.deleteCharAt(messageBuilder.length() - 1); // Remove the last new line

                    c.reply(messageBuilder.toString());
                })
                .register(getCommandName());
    }
}
