package com.github.jeuxjeux20.loupsgarous.commands.debug;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.commands.InGameHandlerCondition;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LGBoxContentsCommand implements HelperCommandRegisterer {
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGBoxContentsCommand(InGameHandlerCondition inGameHandlerCondition) {
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .assertPermission("loupsgarous.debug.boxcontents")
                .description("Prints the box contents into the console.")
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lg_debug_boxcontents");
    }

    private void handle(CommandContext<Player> context, LGPlayer player,
                        LGGameOrchestrator orchestrator) {
        String contentsString = orchestrator.getGameBox().getContentsString();

        System.out.println("Contents of game " + orchestrator + ":");
        System.out.println(contentsString);

        context.reply(ChatColor.GREEN +
                      "Please check the console to... actually know what's in the box.");
    }
}
