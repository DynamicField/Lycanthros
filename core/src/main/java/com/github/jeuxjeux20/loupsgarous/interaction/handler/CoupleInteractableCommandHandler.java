package com.github.jeuxjeux20.loupsgarous.interaction.handler;

import com.github.jeuxjeux20.loupsgarous.chat.LGMessages;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.interaction.Pick;
import com.github.jeuxjeux20.loupsgarous.interaction.Couple;
import com.github.jeuxjeux20.loupsgarous.Check;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CoupleInteractableCommandHandler implements InteractableCommandHandler<Pick<Couple>> {
    @Override
    public void configure(FunctionalCommandBuilder<Player> builder) {
        builder.assertUsage("<partenaire1> <partenaire2>", "{usage}");
    }

    @Override
    public void pick(CommandContext<Player> context, LGPlayer player, Pick<Couple> interactable, LGGameOrchestrator orchestrator) {
        String partner1Name = context.arg(0).value().orElseThrow(AssertionError::new);
        String partner2Name = context.arg(1).value().orElseThrow(AssertionError::new);

        createCouple(partner1Name, partner2Name, orchestrator, context.sender()).ifPresent(couple -> {
            Check check = interactable.conditions().checkPick(player, couple);

            if (check.isSuccess()) {
                interactable.pick(player, couple);
            }
            else {
                context.reply(ChatColor.RED + check.getErrorMessage());
            }
        });
    }

    private Optional<Couple> createCouple(String partner1Name, String partner2Name,
                                          LGGameOrchestrator orchestrator, Player sender) {
        Optional<LGPlayer> partner1 = orchestrator.findByName(partner1Name);
        if (!partner1.isPresent()) {
            sender.sendMessage(LGMessages.cannotFindPlayer(partner1Name));
            return Optional.empty();
        }

        Optional<LGPlayer> partner2 = orchestrator.findByName(partner2Name);
        if (!partner2.isPresent()) {
            sender.sendMessage(LGMessages.cannotFindPlayer(partner2Name));
            return Optional.empty();
        }

        if (partner1.equals(partner2)) {
            sender.sendMessage(ChatColor.RED + "Impossible d'avoir un couple avec deux mêmes personnes !");
            return Optional.empty();
        }

        Couple couple = new Couple(partner1.get(), partner2.get());
        return Optional.of(couple);
    }
}
