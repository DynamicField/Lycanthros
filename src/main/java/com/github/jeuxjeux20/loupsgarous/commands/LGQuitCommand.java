package com.github.jeuxjeux20.loupsgarous.commands;

import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LGQuitCommand implements HelperCommandRegisterer {
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGQuitCommand(InGameHandlerCondition inGameHandlerCondition) {
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lgquit", "lg quit");
    }

    private void handle(CommandContext<Player> context, LGPlayer player, LGGameOrchestrator orchestrator) {
        if (orchestrator.lobby().removePlayer(player)) {
            context.reply(ChatColor.GREEN + "Vous avez quitté la partie.");
        } else {
            context.reply(ChatColor.RED + "Impossible de quitter la partie.");
        }
    }
}
