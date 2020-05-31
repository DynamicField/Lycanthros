package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.LGMessages;
import com.github.jeuxjeux20.loupsgarous.game.*;
import com.github.jeuxjeux20.loupsgarous.game.cards.revealers.CardRevealer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.revealers.TeamRevealer;
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
    private final CardRevealer cardRevealer;
    private final TeamRevealer teamRevealer;

    @Inject
    LGPlayersCommand(LGGameManager gameManager, CardRevealer cardRevealer, TeamRevealer teamRevealer) {
        this.gameManager = gameManager;
        this.cardRevealer = cardRevealer;
        this.teamRevealer = teamRevealer;
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
                    LGPlayer sender = game.get().getPlayer();
                    LGGameOrchestrator orchestrator = game.get().getOrchestrator();
                    Set<LGPlayer> players = orchestrator.getGame().getPlayers();

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

                        if (cardRevealer.willReveal(player, sender, orchestrator)) {
                            messageBuilder.append(" (")
                                    .append(player.getCard().getColor())
                                    .append(player.getCard().getName())
                                    .append(ChatColor.DARK_AQUA)
                                    .append(")");
                        }

                        for (LGTeam team : teamRevealer.getTeamsRevealed(player, sender, orchestrator)) {
                            messageBuilder.append(ChatColor.YELLOW)
                                    .append(" [")
                                    .append(team.getColor())
                                    .append(team.getName())
                                    .append(ChatColor.YELLOW)
                                    .append("]");
                        }

                        messageBuilder.append('\n');
                    }

                    messageBuilder.deleteCharAt(messageBuilder.length() - 1); // Remove the last new line

                    c.reply(messageBuilder.toString());
                })
                .register(getCommandName());
    }
}
