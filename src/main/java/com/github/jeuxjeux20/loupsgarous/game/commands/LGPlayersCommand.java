package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.revealers.CardRevealer;
import com.github.jeuxjeux20.loupsgarous.game.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.game.teams.revealers.TeamRevealer;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.banner;

public class LGPlayersCommand implements HelperCommandRegisterer {
    private final CardRevealer cardRevealer;
    private final TeamRevealer teamRevealer;
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGPlayersCommand(CardRevealer cardRevealer, TeamRevealer teamRevealer,
                     InGameHandlerCondition inGameHandlerCondition) {
        this.cardRevealer = cardRevealer;
        this.teamRevealer = teamRevealer;
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lgplayers", "lg players");
    }

    private void handle(CommandContext<Player> context, LGPlayer sender, LGGameOrchestrator orchestrator) {
        Set<LGPlayer> players = orchestrator.game().getPlayers();

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

        context.reply(messageBuilder.toString());
    }
}
