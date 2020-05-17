package com.github.df.loupsgarous.commands;

import com.github.df.loupsgarous.cards.composition.CompositionGui;
import com.github.df.loupsgarous.game.LGGameOrchestrator;
import com.github.df.loupsgarous.game.LGPlayer;
import com.google.inject.Inject;
import me.lucko.helper.Commands;
import me.lucko.helper.command.context.CommandContext;
import org.bukkit.entity.Player;

import static com.github.df.loupsgarous.chat.LGChatStuff.error;

public class LGLobbyCommand implements HelperCommandRegisterer {
    private final InGameHandlerCondition inGameHandlerCondition;

    @Inject
    LGLobbyCommand(InGameHandlerCondition inGameHandlerCondition) {
        this.inGameHandlerCondition = inGameHandlerCondition;
    }

    @Override
    public void register() {
        Commands.create()
                .assertPlayer()
                .handler(inGameHandlerCondition.wrap(this::handle))
                .register("lglobby", "lg lobby");
    }

    private void handle(CommandContext<Player> context, LGPlayer player, LGGameOrchestrator orchestrator) {
        if (!orchestrator.allowsJoin()) {
            context.reply(error("La partie a déjà commencé."));
            return;
        }

        LGPlayer owner = orchestrator.getOwner();
        if (owner == null ||
            owner.getPlayerUUID() != context.sender().getUniqueId()) {
            context.reply(error("Vous n'êtes pas le maître de la partie."));
            return;
        }

        owner.minecraft(mcPlayer -> new CompositionGui(mcPlayer, orchestrator).open());
    }
}
