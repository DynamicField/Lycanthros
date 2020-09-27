package com.github.jeuxjeux20.loupsgarous.commands.debug;

import com.github.jeuxjeux20.loupsgarous.chat.LGChatStuff;
import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.commands.InGameHandlerCondition;
import com.github.jeuxjeux20.loupsgarous.interaction.Interactable;
import com.github.jeuxjeux20.loupsgarous.interaction.InteractableKey;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class LGCurrentInteractablesCommand implements HelperCommandRegisterer {
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGCurrentInteractablesCommand(InGameHandlerCondition inGameHandlerCondition) {
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .assertPermission("loupsgarous.debug.currentinteractables")
                .description("Gets the current interactables of the game.")
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lg_debug_currentinteractables");
    }

    private void handle(CommandContext<Player> context, LGPlayer player, LGGameOrchestrator orchestrator) {
        context.reply(LGChatStuff.banner("Current interactables"));

        StringBuilder builder = new StringBuilder();

        for (Map.Entry<InteractableKey<?>, Interactable> entry : orchestrator.interactables().getAll().entries()) {
            if (builder.length() != 0) {
                builder.append("\n");
            }

            String entryLine = ChatColor.AQUA + entry.getKey().getName() +
                               ChatColor.GREEN + " -> " +
                               ChatColor.GOLD + entry.getValue();

            builder.append(entryLine);
        }

        if (builder.length() == 0) {
            builder.append("[Empty]");
        }

        context.reply(builder.toString());
    }
}
