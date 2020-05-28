package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.util.CompositionFormatUtil;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.banner;

@CommandName("lgcomposition")
public class LGCompositionCommand implements AnnotatedCommandConfigurator {
    private final LGGameManager gameManager;

    @Inject
    LGCompositionCommand(LGGameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        Commands.create()
                .assertPlayer()
                .handler(c -> {
                    Optional<LGPlayerAndGame> game = gameManager.getPlayerInGame(c.sender());
                    if (!game.isPresent()) {
                        c.reply("&cVous n'êtes pas en partie.");
                        return;
                    }
                    LGGameOrchestrator orchestrator = game.get().getOrchestrator();

                    String message = banner("Composition") + '\n' +
                                     CompositionFormatUtil.format(orchestrator.getCurrentComposition());

                    c.reply(message);
                })
                .register(getCommandName());
    }
}
