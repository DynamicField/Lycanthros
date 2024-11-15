package com.github.df.loupsgarous.commands;

import com.github.df.loupsgarous.game.LGGameManager;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.util.PaginationUtils;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.ChatColor;

import static com.github.df.loupsgarous.chat.LGChatStuff.banner;

public class LGListCommand implements HelperCommandRegisterer {
    private final LGGameManager gameManager;

    @Inject
    LGListCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPermission("loupsgarous.game.list",
                        ChatColor.RED + "Vous n'avez pas la permission de voir les parties :(")
                .assertUsage("[page]", "{usage}")
                .handler(c -> {
                    int page = Math.min(c.arg(0).parse(Integer.class).orElse(1), 1);
                    int itemsPerPage = 8;
                    int itemsCount = gameManager.getAll().size();
                    int pageCount = PaginationUtils.getPageCount(gameManager.getAll(), itemsPerPage);

                    if (pageCount == 0) {
                        c.reply("&bIl n'y a aucune partie en cours !");
                        return;
                    }

                    final StringBuilder messageBuilder = new StringBuilder();

                    String header = String.format(
                            banner("Parties en cours %d/%d (%d parties)") + "\n",
                            page, pageCount, itemsCount);

                    messageBuilder.append(header);

                    PaginationUtils.in(gameManager.getAll(), page, itemsPerPage).forEach(x -> {
                        LGGameOrchestrator orchestrator = x.getValue();

                        long gameNumber = x.getIndex() + 1;
                        String gameId = orchestrator.getId();

                        messageBuilder.append(ChatColor.RESET)
                                .append(gameNumber)
                                .append(". ")
                                .append(ChatColor.GOLD)
                                .append(gameId)
                                .append(ChatColor.BLUE)
                                .append(" (")
                                .append(orchestrator.getAlivePlayers().count())
                                .append('/')
                                .append(orchestrator.getPlayers().size())
                                .append(')')
                                .append('\n');
                    });

                    messageBuilder.append(ChatColor.RESET)
                            .append(ChatColor.ITALIC)
                            .append(ChatColor.DARK_GRAY)
                            .append("Utilisez /lglist <page> pour afficher une autre page.");

                    c.reply(messageBuilder.toString());
                })
                .register("lglist", "lg list");
    }
}
