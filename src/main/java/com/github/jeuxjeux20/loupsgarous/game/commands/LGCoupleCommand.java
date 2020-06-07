package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.LGMessages;
import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.stages.CupidonCoupleStage;
import com.github.jeuxjeux20.loupsgarous.util.SafeResult;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.entity.Player;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;

public class LGCoupleCommand implements HelperCommandRegisterer {
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGCoupleCommand(LGGameManager gameManager, InGameHandlerCondition inGameHandlerCondition) {
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .assertUsage("<partenaire1> <partenaire2>", "On a dit : {usage}")
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lgcouple", "lg couple");
    }

    private void handle(CommandContext<Player> c, LGPlayer player, LGGameOrchestrator orchestrator) {
        String partner1Name = c.arg(0).value().orElseThrow(AssertionError::new);
        String partner2Name = c.arg(1).value().orElseThrow(AssertionError::new);

        Optional<SafeResult<CupidonCoupleStage>> maybeStage = orchestrator.stages().current()
                .getSafeComponent(CupidonCoupleStage.class, x -> x.canPlayerCreateCouple(player));

        CupidonCoupleStage coupleStage = maybeStage
                .flatMap(SafeResult::getValueOptional)
                .orElse(null);

        if (coupleStage == null) {
            String errorMessage = maybeStage
                    .flatMap(SafeResult::getErrorMessageOptional)
                    .orElse("Ce n'est pas l'heure !");

            c.reply(error(errorMessage));
            return;
        }

        createCouple(partner1Name, partner2Name, orchestrator, c.sender()).ifPresent(couple -> {
            if (coupleStage.canCreateCouple(player, couple).sendMessageOnError(c.sender()))
                return;

            coupleStage.createCouple(player, couple);
        });
    }

    private Optional<CupidonCoupleStage.Couple> createCouple(String partner1Name, String partner2Name,
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

        CupidonCoupleStage.Couple couple = new CupidonCoupleStage.Couple(partner1.get(), partner2.get());
        return Optional.of(couple);
    }
}
