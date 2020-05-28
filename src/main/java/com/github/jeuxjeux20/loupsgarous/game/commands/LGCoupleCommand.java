package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.LGMessages;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.stages.CupidonCoupleStage;
import com.github.jeuxjeux20.loupsgarous.util.Check;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;

@CommandName("lgcouple")
public class LGCoupleCommand implements AnnotatedCommandConfigurator {
    private final LGGameManager gameManager;

    @Inject
    LGCoupleCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        Commands.create()
                .assertPlayer()
                .assertUsage("<partenaire1> <partenaire2>", "On a dit : {usage}")
                .handler(c -> {
                    Optional<LGPlayerAndGame> maybeGame = gameManager.getPlayerInGame(c.sender());
                    if (!maybeGame.isPresent()) {
                        c.reply("&cVous n'êtes pas en partie.");
                        return;
                    }
                    LGGameOrchestrator orchestrator = maybeGame.get().getOrchestrator();
                    LGPlayer player = maybeGame.get().getPlayer();

                    String partner1Name = c.arg(0).value().orElseThrow(AssertionError::new);
                    String partner2Name = c.arg(1).value().orElseThrow(AssertionError::new);

                    AtomicReference<Optional<Check>> check = new AtomicReference<>();

                    Optional<CupidonCoupleStage> maybeCoupleStage = orchestrator.stages().current()
                            .getComponent(CupidonCoupleStage.class, x -> x.canPlayerCreateCouple(player), check);

                    if (!maybeCoupleStage.isPresent()) {
                        String errorMessage = check.get().map(Check::getErrorMessage).orElse("Ce n'est pas l'heure !");
                        c.reply(error(errorMessage));
                        return;
                    }

                    CupidonCoupleStage coupleStage = maybeCoupleStage.get();

                    createCouple(partner1Name, partner2Name, orchestrator, c.sender()).ifPresent(couple -> {
                        if (coupleStage.canCreateCouple(player, couple).sendMessageOnError(c.sender()))
                            return;

                        coupleStage.createCouple(player, couple);
                    });
                })
                .register(getCommandName());
    }

    private Optional<CupidonCoupleStage.Couple> createCouple(String partner1Name, String partner2Name,
                                                             LGGameOrchestrator orchestrator, Player sender) {

        Optional<LGPlayer> partner1 = orchestrator.getGame().findByName(partner1Name);
        if (!partner1.isPresent()) {
            sender.sendMessage(LGMessages.cannotFindPlayer(partner1Name));
            return Optional.empty();
        }

        Optional<LGPlayer> partner2 = orchestrator.getGame().findByName(partner2Name);
        if (!partner2.isPresent()) {
            sender.sendMessage(LGMessages.cannotFindPlayer(partner2Name));
            return Optional.empty();
        }

        CupidonCoupleStage.Couple couple = new CupidonCoupleStage.Couple(partner1.get(), partner2.get());
        return Optional.of(couple);
    }
}
