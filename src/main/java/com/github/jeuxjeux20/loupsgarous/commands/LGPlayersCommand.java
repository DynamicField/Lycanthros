package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.game.LGGame;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.cards.LGCard;
import com.github.jeuxjeux20.loupsgarous.cards.revealers.CardRevealer;
import com.github.jeuxjeux20.loupsgarous.tags.LGTag;
import com.github.jeuxjeux20.loupsgarous.tags.revealers.TagRevealer;
import com.github.jeuxjeux20.loupsgarous.teams.LGTeam;
import com.github.jeuxjeux20.loupsgarous.teams.revealers.TeamRevealer;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.banner;

public class LGPlayersCommand implements HelperCommandRegisterer {
    private static final String LABEL_SEPARATOR = ChatColor.RESET + " - ";

    private final CardRevealer cardRevealer;
    private final TeamRevealer teamRevealer;
    private final TagRevealer tagRevealer;
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGPlayersCommand(CardRevealer cardRevealer,
                     TeamRevealer teamRevealer,
                     TagRevealer tagRevealer,
                     InGameHandlerCondition inGameHandlerCondition) {
        this.cardRevealer = cardRevealer;
        this.teamRevealer = teamRevealer;
        this.tagRevealer = tagRevealer;
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

            if (player.isDead())
                messageBuilder.append(ChatColor.STRIKETHROUGH);

            messageBuilder.append(player.getName());

            List<String> labels = getLabels(sender, player, orchestrator.game());
            if (!labels.isEmpty()) {
                messageBuilder.append(" ")
                        .append(String.join(LABEL_SEPARATOR, labels));
            }

            messageBuilder.append('\n');
        }

        messageBuilder.deleteCharAt(messageBuilder.length() - 1); // Remove the last new line

        context.reply(messageBuilder.toString());
    }

    private List<String> getLabels(LGPlayer sender, LGPlayer player, LGGame game) {
        List<String> labels = new ArrayList<>();

        if (cardRevealer.willReveal(sender, player, game)) {
            LGCard card = player.getCard();

            labels.add(card.getColor() + card.getName());
        }

        for (LGTeam team : teamRevealer.getTeamsRevealed(sender, player, game)) {
            labels.add(team.getColor() + team.getName());
        }

        for (LGTag tag : tagRevealer.getTagsRevealed(sender, player, game)) {
            labels.add(tag.getColor() + tag.getName());
        }

        return labels;
    }
}
