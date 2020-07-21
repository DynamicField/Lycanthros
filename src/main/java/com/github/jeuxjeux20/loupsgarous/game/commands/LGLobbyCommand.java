package com.github.jeuxjeux20.loupsgarous.game.commands;

import com.github.jeuxjeux20.loupsgarous.commands.HelperCommandRegisterer;
import com.github.jeuxjeux20.loupsgarous.game.LGGameOrchestrator;
import com.github.jeuxjeux20.loupsgarous.game.LGPlayer;
import com.github.jeuxjeux20.loupsgarous.game.cards.composition.gui.CompositionGuiOpener;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.entity.Player;

import static com.github.jeuxjeux20.loupsgarous.LGChatStuff.error;

public class LGLobbyCommand implements HelperCommandRegisterer {
    private final InGameHandlerCondition inGameHandlerCondition;
    private final CompositionGuiOpener compositionGuiOpener;

    @Inject
    LGLobbyCommand(InGameHandlerCondition inGameHandlerCondition, CompositionGuiOpener compositionGuiOpener) {
        this.inGameHandlerCondition = inGameHandlerCondition;
        this.compositionGuiOpener = compositionGuiOpener;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lglobby", "lg lobby");
    }

    private void handle(CommandContext<Player> context, LGPlayer player, LGGameOrchestrator orchestrator) {
        if (orchestrator.lobby().isLocked()) {
            context.reply(error("La partie a déjà commencé."));
            return;
        }
        if (orchestrator.lobby().getOwner().getPlayerUUID() != context.sender().getUniqueId()) {
            context.reply(error("Vous n'êtes pas le maître de la partie."));
            return;
        }

        compositionGuiOpener.open(orchestrator);
    }
}
