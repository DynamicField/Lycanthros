package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.guicybukkit.command.AnnotatedCommandConfigurator;
import com.github.jeuxjeux20.guicybukkit.command.CommandName;
import com.github.jeuxjeux20.loupsgarous.LGMessages;
import com.github.jeuxjeux20.loupsgarous.game.LGGameManager;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayerAndGame;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.gui.CompositionGui;
import com.github.jeuxjeux20.loupsgarous.game.events.LGEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.LGGameDeletedEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.LGGameStartEvent;
import com.github.jeuxjeux20.loupsgarous.game.events.lobby.LGLobbyOwnerChangeEvent;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.Events;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;

@CommandName("lglobby")
public class LGLobbyCommand implements AnnotatedCommandConfigurator {
    private final LGGameManager gameManager;
    private final CompositionGui.Factory compositionGuiFactory;

    @Inject
    LGLobbyCommand(LGGameManager gameManager, CompositionGui.Factory compositionGuiFactory) {
        this.gameManager = gameManager;
        this.compositionGuiFactory = compositionGuiFactory;
    }

    @Override
    public void configureCommand(@NotNull PluginCommand command) {
        Commands.create()
                .assertPlayer()
                .handler(c -> {
                    Optional<LGPlayerAndGame> game = gameManager.getPlayerInGame(c.sender());
                    if (!game.isPresent()) {
                        c.reply(LGMessages.NOT_IN_GAME);
                        return;
                    }
                    LGGameOrchestrator orchestrator = game.get().getOrchestrator();

                    if (orchestrator.lobby().isLocked()) {
                        c.reply(error("La partie a déjà commencé."));
                        return;
                    }
                    if (orchestrator.lobby().getOwner() != c.sender()) {
                        c.reply(error("Vous n'êtes pas le maître de la partie."));
                        return;
                    }

                    orchestrator.lobby().getMutableComposition().ifPresent(composition -> {
                        CompositionGui gui = compositionGuiFactory.create(c.sender(), composition);
                        gui.open();

                        Events.merge(LGEvent.class,
                                LGGameStartEvent.class, LGGameDeletedEvent.class, LGLobbyOwnerChangeEvent.class)
                                .expireIf(x -> !gui.isValid())
                                .filter(x -> x.getOrchestrator() == orchestrator)
                                .handler(e -> gui.close());

                    });
                })
                .register(getCommandName());
    }
}
