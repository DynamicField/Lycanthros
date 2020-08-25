package com.github.jeuxjeux20.loupsgarous.interaction.handler;

import com.github.jeuxjeux20.loupsgarous.LGMessages;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.Pick;
import com.github.jeuxjeux20.loupsgarous.Check;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SinglePlayerInteractableCommandHandler implements InteractableCommandHandler<Pick<LGPlayer>> {
    @Override
    public void configure(FunctionalCommandBuilder<Player> builder) {
        builder.assertUsage("<player>", "C'est pas comme ça que ça marche ! {usage}");
    }

    @Override
    public void pick(CommandContext<Player> context, LGPlayer player, Pick<LGPlayer> interactable, LGGameOrchestrator orchestrator) {
        String targetName = context.arg(0).value().orElseThrow(AssertionError::new);

        Optional<LGPlayer> maybeTarget = orchestrator.findByName(targetName);

        maybeTarget.ifPresent(target -> {
            Check check = interactable.conditions().checkPick(player, target);

            if (check.isSuccess()) {
                interactable.pick(player, target);
            }
            else {
                context.reply(ChatColor.RED + check.getErrorMessage());
            }
        });

        if (!maybeTarget.isPresent())
            context.reply(LGMessages.cannotFindPlayer(targetName));
    }
}
