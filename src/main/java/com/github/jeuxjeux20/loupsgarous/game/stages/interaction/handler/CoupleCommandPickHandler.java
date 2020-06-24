package com.github.jeuxjeux20.loupsgarous.game.stages.interaction.handler;

import com.github.jeuxjeux20.loupsgarous.LGMessages;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Couple;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.CoupleCreator;
import com.github.jeuxjeux20.loupsgarous.game.stages.interaction.Pickable;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import me.lucko.helper.command.context.CommandContext;
import me.lucko.helper.command.functional.FunctionalCommandBuilder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;

public class CoupleCommandPickHandler implements CommandPickHandler<Pickable<Couple>> {
    @Override
    public void configure(FunctionalCommandBuilder<Player> builder) {
        builder.assertUsage("<partenaire1> <partenaire2>", "{usage}");
    }

    @Override
    public void pick(CommandContext<Player> context, LGPlayer player, Pickable<Couple> pickable, LGGameOrchestrator orchestrator) {
        String partner1Name = context.arg(0).value().orElseThrow(AssertionError::new);
        String partner2Name = context.arg(1).value().orElseThrow(AssertionError::new);

        Optional<SafeResult<CoupleCreator>> maybeCoupleCreator = orchestrator.stages().current()
                .getSafeComponent(CoupleCreator.class, x -> x.conditions().checkPicker(player));

        CoupleCreator coupleCreator = maybeCoupleCreator
                .flatMap(SafeResult::getValueOptional)
                .orElse(null);

        if (coupleCreator == null) {
            String errorMessage = maybeCoupleCreator
                    .flatMap(SafeResult::getErrorMessageOptional)
                    .orElse("Ce n'est pas l'heure !");

            context.reply(error(errorMessage));
            return;
        }

        createCouple(partner1Name, partner2Name, orchestrator, context.sender()).ifPresent(couple -> {
            Check check = coupleCreator.conditions().checkPick(player, couple);

            if (check.isSuccess()) {
                coupleCreator.pick(player, couple);
            }
            else {
                context.reply(ChatColor.RED + check.getErrorMessage());
            }
        });
    }

    private Optional<Couple> createCouple(String partner1Name, String partner2Name,
                                          LGGameOrchestrator orchestrator, Player sender) {
        Optional<LGPlayer> partner1 = orchestrator.game().findByName(partner1Name);
        if (!partner1.isPresent()) {
            sender.sendMessage(LGMessages.cannotFindPlayer(partner1Name));
            return Optional.empty();
        }

        Optional<LGPlayer> partner2 = orchestrator.game().findByName(partner2Name);
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
