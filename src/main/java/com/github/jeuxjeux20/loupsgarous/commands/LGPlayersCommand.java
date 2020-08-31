package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.tags.LGTag;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.banner;
import static com.github.jeuxjeux20.loupsgarous.extensibility.LGExtensionPoints.*;

public class LGPlayersCommand implements HelperCommandRegisterer {
    private static final String LABEL_SEPARATOR = ChatColor.RESET + " - ";

    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGPlayersCommand(InGameHandlerCondition inGameHandlerCondition) {
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
        Set<LGPlayer> players = orchestrator.getPlayers();

        StringBuilder messageBuilder = new StringBuilder()
                .append(banner("Liste des joueurs"))
                .append('\n');

        for (LGPlayer player : players) {
            messageBuilder.append(ChatColor.RESET);

            if (player.isDead())
                messageBuilder.append(ChatColor.STRIKETHROUGH);

            messageBuilder.append(player.getName());

            List<String> labels = getLabels(sender, player, orchestrator);
            if (!labels.isEmpty()) {
                messageBuilder.append(" ")
                        .append(String.join(LABEL_SEPARATOR, labels));
            }

            messageBuilder.append('\n');
        }

        messageBuilder.deleteCharAt(messageBuilder.length() - 1); // Remove the last new line

        context.reply(messageBuilder.toString());
    }

    private List<String> getLabels(LGPlayer sender, LGPlayer player, LGGameOrchestrator orchestrator) {
        List<String> labels = new ArrayList<>();

        if (orchestrator.getGameBundle().handler(CARD_REVEALERS).willReveal(sender, player)) {
            LGCard card = player.getCard();

            labels.add(card.getColor() + card.getName());
        }

        for (LGTeam team : orchestrator.getGameBundle().handler(TEAM_REVEALERS).getTeamsRevealed(sender, player)) {
            labels.add(team.getColor() + team.getName());
        }

        for (LGTag tag : orchestrator.getGameBundle().handler(TAG_REVEALERS).getTagsRevealed(sender, player)) {
            labels.add(tag.getColor() + tag.getName());
        }

        return labels;
    }
}
